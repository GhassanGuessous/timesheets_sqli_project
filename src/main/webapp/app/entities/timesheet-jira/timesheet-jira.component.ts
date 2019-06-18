import { Component, OnInit } from '@angular/core';
import { ITeam } from 'app/shared/model/team.model';
import { AccountService } from 'app/core';
import { TeamService } from 'app/entities/team';
import { TimesheetJiraService } from 'app/entities/timesheet-jira/timesheet-jira.service';
import { DatePipe } from '@angular/common';
import { ITbpRequestBody, TbpRequestBody } from 'app/shared/model/tbp-request-body';
import { AuthJiraModalService } from 'app/core/authJira/auth-jira-modal.service';

@Component({
    selector: 'jhi-timesheet-jira',
    templateUrl: './timesheet-jira.component.html',
    styles: []
})
export class TimesheetJiraComponent implements OnInit {
    private currentAccount: any;
    private myTeam: ITeam;
    private allTeams: Array<ITeam>;
    private predicate: any;
    private reverse: any;
    private jiraWorklogs: any;
    tbpRequestBody: ITbpRequestBody = new TbpRequestBody();
    private worklogDays: Map<any, Array<number>> = new Map<any, Array<number>>();

    constructor(
        protected service: TimesheetJiraService,
        protected accountService: AccountService,
        protected teamService: TeamService,
        private authJiraModalService: AuthJiraModalService,
        private datePipe: DatePipe
    ) {}

    ngOnInit() {
        this.initialize();
        this.accountService.identity().then(account => {
            this.currentAccount = account;
            if (this.isAdmin()) {
                this.loadAll();
            } else {
                this.loadDelcoTeam(account.id);
            }
        });
    }

    loadAll() {
        this.teamService.findAllTeamsWithoutPagination().subscribe(res => {
            this.allTeams = res.body;
        });
    }

    loadDelcoTeam(id: bigint) {
        this.teamService.findByDelco(id).subscribe(data => {
            this.myTeam = data.body;
            this.tbpRequestBody.idTbp = this.myTeam.displayName;
        });
    }

    isAdmin() {
        return this.currentAccount.authorities.includes('ROLE_ADMIN');
    }

    private initialize() {
        this.today();
        this.previousMonth();
    }

    private previousMonth() {
        const dateFormat = 'yyyy-MM-dd';
        let fromDate: Date = new Date();

        if (fromDate.getMonth() === 0) {
            fromDate = new Date(fromDate.getFullYear() - 1, 11, fromDate.getDate());
        } else {
            fromDate = new Date(fromDate.getFullYear(), fromDate.getMonth() - 1, fromDate.getDate());
        }

        this.tbpRequestBody.startDate = this.datePipe.transform(fromDate, dateFormat);
    }

    private today() {
        const dateFormat = 'yyyy-MM-dd';
        // Today + 1 day - needed if the current day must be included
        const today: Date = new Date();
        today.setDate(today.getDate() + 1);
        const date = new Date(today.getFullYear(), today.getMonth(), today.getDate());
        this.tbpRequestBody.endDate = this.datePipe.transform(date, dateFormat);
    }

    getTimesheet() {
        if (localStorage.getItem('isJiraAuthenticated') === 'false') {
            this.authenticateThenGetTimesheet();
        } else {
            this.getTimesheetWhenIsAlreadyAuthenticated();
        }
    }

    private authenticateThenGetTimesheet() {
        this.authJiraModalService.open(this.tbpRequestBody).then(
            result => {
                this.jiraWorklogs = result;
            },
            reason => {
                console.log(reason);
            }
        );
    }

    private getTimesheetWhenIsAlreadyAuthenticated() {
        this.tbpRequestBody.username = localStorage.getItem('jiraUsername');
        this.tbpRequestBody.password = localStorage.getItem('jiraPassword');
        this.service.getTimesheet(this.tbpRequestBody).subscribe(res => {
            this.jiraWorklogs = res.body;
            console.log(res.body);
            for (let i = 0; i < this.jiraWorklogs.length; i++) {
                this.initializeDays(this.jiraWorklogs[i]);
            }
        });
    }

    private initializeDays(worklog: any) {
        const days: Array<number> = new Array<number>();
        worklog.collaboratorWorklogs.forEach(collabWorklog => {
            collabWorklog.jiraDailyWorklogs.forEach(daily => {
                days.push(daily.day);
            });
        });
        this.removeDuplecates(days, worklog);
    }

    private removeDuplecates(days: Array<number>, worklog: any) {
        days = Array.from(new Set(days)).sort((a, b) => a - b);
        this.worklogDays.set(worklog, days);
    }

    private isFilledImputation(): boolean {
        return this.jiraWorklogs !== undefined;
    }

    getWorklogTimeSpent(timeSpent: any) {
        let timespent = '';
        timespent += timeSpent.weeks === 0 ? '' : timeSpent.weeks + 'w ';
        timespent += timeSpent.days === 0 ? '' : timeSpent.days + 'd ';
        timespent += timeSpent.hours === 0 ? '' : timeSpent.hours + 'h ';
        timespent += timeSpent.minutes === 0 ? '' : timeSpent.minutes + 'm';
        return timespent;
    }

    getColor(difference: number): string {
        if (difference < 0) {
            return 'red';
        } else if (difference > 0) {
            return 'orange';
        } else {
            return 'green';
        }
    }
}
