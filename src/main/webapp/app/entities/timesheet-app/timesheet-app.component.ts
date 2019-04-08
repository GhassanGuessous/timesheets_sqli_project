import { Component, OnInit } from '@angular/core';
import { ITeam } from 'app/shared/model/team.model';
import { AccountService } from 'app/core';
import { TeamService } from 'app/entities/team';
import { AppRequestBody, IAppRequestBody } from 'app/shared/model/app-request-body';
import { TimesheetAppService } from './timesheet-app.service';
import { IImputation } from 'app/shared/model/imputation.model';

@Component({
    selector: 'jhi-timesheet-app',
    templateUrl: './timesheet-app.component.html',
    styles: []
})
export class TimesheetAppComponent implements OnInit {
    currentAccount: any;
    myTeam: ITeam;
    allTeams: ITeam[];
    currentYear: number = new Date().getFullYear();
    currentMonth: number = new Date().getMonth();
    appRequestBody: IAppRequestBody = new AppRequestBody('', this.currentYear, this.currentMonth);
    years: Array<number> = [];
    months: Array<number> = [];
    predicate: any;
    reverse: any;
    imputations: any;
    private days: Array<number> = [];

    constructor(protected service: TimesheetAppService, protected accountService: AccountService, protected teamService: TeamService) {}

    ngOnInit() {
        this.initialize();
        console.log(new Date(2019, 2, 0).getDate());
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
        });
    }

    isDelco() {
        return this.currentAccount.authorities.includes('ROLE_DELCO');
    }

    isAdmin() {
        return this.currentAccount.authorities.includes('ROLE_ADMIN');
    }

    getTimesheet() {
        this.service.findAppChargeByTeam(this.appRequestBody).subscribe(res => {
            console.log(res.body);
            this.imputations = res.body;
            this.initializeDays(res.body);
        });
    }

    private initializeDays(res: IImputation) {
        res.monthlyImputations.forEach(monthly => {
            monthly.dailyImputations.forEach(daily => this.days.push(daily.day));
        });
        this.removeDuplecates(this.days);
    }

    private removeDuplecates(days: Array<number>) {
        this.days = Array.from(new Set(days)).sort((a, b) => a - b);
    }

    private initialize() {
        this.initializeYears();
        this.initializeMonth();
    }

    private initializeYears() {
        for (let i = 2015; i <= this.currentYear; i++) {
            this.years.push(i);
        }
    }

    private initializeMonth() {
        for (let i = 1; i <= 12; i++) {
            this.months.push(i);
        }
    }
}
