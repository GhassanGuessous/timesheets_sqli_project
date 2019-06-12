import { Component, OnInit } from '@angular/core';
import { ITeam } from 'app/shared/model/team.model';
import { AppTbpRequestBody, IAppTbpRequestBody } from 'app/shared/model/app-tbp-request-body';
import { AccountService } from 'app/core';
import { TeamService } from 'app/entities/team';
import { AuthTbpModalService } from 'app/core/authTbp/auth-tbp-modal.service';
import { TimesheetJiraService } from 'app/entities/timesheet-jira/timesheet-jira.service';

@Component({
    selector: 'jhi-timesheet-jira',
    templateUrl: './timesheet-jira.component.html',
    styles: []
})
export class TimesheetJiraComponent implements OnInit {
    private currentAccount: any;
    private myTeam: ITeam;
    private allTeams: Array<ITeam>;
    private currentYear: number = new Date().getFullYear();
    private currentMonth: number = new Date().getMonth() + 1;
    private years: Array<number> = [];
    private months: Array<number>;
    private predicate: any;
    private reverse: any;
    private jiraWorklog: any;
    private appTbpRequestBody: IAppTbpRequestBody = new AppTbpRequestBody(null, this.currentYear, this.currentMonth);
    private worklogDays: Map<any, Array<number>> = new Map<any, Array<number>>();

    constructor(
        protected service: TimesheetJiraService,
        protected accountService: AccountService,
        protected teamService: TeamService,
        private authTbpModalService: AuthTbpModalService
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
            this.appTbpRequestBody.team = this.myTeam;
        });
    }

    isAdmin() {
        return this.currentAccount.authorities.includes('ROLE_ADMIN');
    }

    private initialize() {
        this.initializeYears();
        this.initializeMonth();
    }

    private initializeYears() {
        const startImputationsYear = 2015;
        for (let i = startImputationsYear; i <= this.currentYear; i++) {
            this.years.push(i);
        }
    }

    private initializeMonth() {
        let lastMonthInYear = 12;
        this.months = [];
        if (this.appTbpRequestBody.year == this.currentYear) {
            lastMonthInYear = this.currentMonth;
        }
        for (let i = 1; i <= lastMonthInYear; i++) {
            this.months.push(i);
        }
    }

    compare() {
        // if (localStorage.getItem('isTbpAuthenticated') === 'false') {
        //     this.authenticateThenCompare();
        // } else {
        this.getTimesheet();
        // }
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

    private authenticateThenCompare() {
        this.appTbpRequestBody.requestType = 'APP_TBP_COMPARATOR';
        this.authTbpModalService.open(this.appTbpRequestBody).then(
            result => {
                this.jiraWorklog = result;
            },
            reason => {
                console.log(reason);
            }
        );
    }

    private getTimesheet() {
        // this.appTbpRequestBody.username = localStorage.getItem('username');
        // this.appTbpRequestBody.password = localStorage.getItem('password');
        this.service.getTimesheet(this.appTbpRequestBody).subscribe(res => {
            this.jiraWorklog = res.body;
            console.log(res.body);
            this.initializeDays(res.body);
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
        return this.jiraWorklog !== undefined;
    }

    getWorklogTimeSpent(timeSpent: any) {
        let timespent = '';
        timespent += timeSpent.weeks === 0 ? '' : timeSpent.weeks + 'w ';
        timespent += timeSpent.days === 0 ? '' : timeSpent.days + 'd ';
        timespent += timeSpent.hours === 0 ? '' : timeSpent.hours + 'h ';
        timespent += timeSpent.minutes === 0 ? '' : timeSpent.minutes + 'm';
        return timespent;
    }
}
