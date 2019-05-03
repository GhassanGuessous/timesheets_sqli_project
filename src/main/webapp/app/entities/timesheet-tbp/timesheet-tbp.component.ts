import { Component, OnInit } from '@angular/core';
import { AccountService } from 'app/core';
import { ITeam } from 'app/shared/model/team.model';
import { ITbpRequestBody, TbpRequestBody } from 'app/shared/model/tbp-request-body';
import { IImputation } from 'app/shared/model/imputation.model';
import { TeamService } from 'app/entities/team';
import { AuthTbpModalService } from 'app/core/authTbp/auth-tbp-modal.service';
import { TimesheetTbpService } from 'app/entities/timesheet-tbp/timesheet-tbp.service';

@Component({
    selector: 'jhi-timesheet-tbp',
    templateUrl: './timesheet-tbp.component.html',
    styles: []
})
export class TimesheetTbpComponent implements OnInit {
    currentAccount: any;
    tbpRequestBody: ITbpRequestBody = new TbpRequestBody();
    myTeam: ITeam;
    allTeams: ITeam[];
    imputations: any;
    predicate: any;
    reverse: any;
    private imputationDays: Map<IImputation, Array<number>> = new Map<IImputation, Array<number>>();

    constructor(
        protected accountService: AccountService,
        protected teamService: TeamService,
        private authTbpModalService: AuthTbpModalService,
        protected timesheetTbpService: TimesheetTbpService
    ) {}

    ngOnInit() {
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
            this.tbpRequestBody.idTbp = this.myTeam.idTbp;
        });
    }

    isAdmin() {
        return this.currentAccount.authorities.includes('ROLE_ADMIN');
    }

    getTimesheet() {
        if (localStorage.getItem('isTbpAuthenticated') === 'false') {
            this.authenticateThenGetTimesheet();
        } else {
            this.getTimesheetWhenIsAlreadyAuthenticated();
        }
    }

    private authenticateThenGetTimesheet() {
        this.tbpRequestBody.requestType = 'TBP';
        this.authTbpModalService.open(this.tbpRequestBody).then(
            result => {
                this.imputations = result;
                this.initializeDaysForImputation();
            },
            reason => {
                console.log(reason);
            }
        );
    }

    private getTimesheetWhenIsAlreadyAuthenticated() {
        this.tbpRequestBody.username = localStorage.getItem('username');
        this.tbpRequestBody.password = localStorage.getItem('password');

        this.timesheetTbpService.findTbpChargeByTeam(this.tbpRequestBody).subscribe(res => {
            this.imputations = res.body;
            this.initializeDaysForImputation();
        });
    }

    private initializeDaysForImputation() {
        for (let i = 0; i < this.imputations.length; i++) {
            this.initializeDays(this.imputations[i]);
        }
    }

    private initializeDays(imputation: any) {
        const days: Array<number> = new Array<number>();
        imputation.monthlyImputations.forEach(monthly => {
            monthly.dailyImputations.forEach(daily => {
                days.push(daily.day);
            });
        });
        this.removeDuplecates(days, imputation);
    }

    private removeDuplecates(days: Array<number>, imputation: IImputation) {
        days = Array.from(new Set(days)).sort((a, b) => a - b);
        this.imputationDays.set(imputation, days);
    }
}
