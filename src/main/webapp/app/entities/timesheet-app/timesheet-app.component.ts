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
    private currentAccount: any;
    private myTeam: ITeam;
    private allTeams: ITeam[];
    private currentYear: number = new Date().getFullYear();
    private currentMonth: number = new Date().getMonth();
    private appRequestBody: IAppRequestBody = new AppRequestBody(
        '',
        this.currentYear,
        this.currentMonth,
        new Date(this.currentYear, this.currentMonth, 0).getDate()
    );
    private years: Array<number> = [];
    private months: Array<number> = [];
    private manDays: Array<number> = [];
    private predicate: any;
    private reverse: any;
    private imputations: any;
    private days: Array<number> = [];

    constructor(protected service: TimesheetAppService, protected accountService: AccountService, protected teamService: TeamService) {}

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
        this.initializeManDays();
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

    private initializeManDays() {
        for (let i = 1; i <= 40; i++) {
            this.manDays.push(i);
        }
    }
}
