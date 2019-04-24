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
    private currentMonth: number = new Date().getMonth() + 1;
    private numberOfDaysOfCurrentMonth: number = new Date(this.currentYear, this.currentMonth, 0).getDate();
    private appRequestBody: IAppRequestBody = new AppRequestBody(
        '',
        this.currentYear,
        this.currentMonth,
        1,
        this.numberOfDaysOfCurrentMonth
    );
    private years: Array<number> = [];
    private months: Array<number> = [];
    private manDays: Array<number> = [];
    private days: Array<number>;
    private imputationDays: Map<IImputation, Array<number>> = new Map<IImputation, Array<number>>();
    private predicate: any;
    private reverse: any;
    private imputations: any;

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

    private loadAll() {
        this.teamService.findAllTeamsWithoutPagination().subscribe(res => {
            this.allTeams = res.body;
        });
    }

    private loadDelcoTeam(id: bigint) {
        this.teamService.findByDelco(id).subscribe(data => {
            this.myTeam = data.body;
            this.appRequestBody.agresso = this.myTeam.agresso;
        });
    }

    private isAdmin() {
        return this.currentAccount.authorities.includes('ROLE_ADMIN');
    }

    private getTimesheet() {
        this.service.findAppChargeByTeam(this.appRequestBody).subscribe(res => {
            this.imputations = res.body;
            for (let i = 0; i < this.imputations.length; i++) {
                this.initializeDays(this.imputations[i]);
            }
        });
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

    private initialize() {
        this.initializeYears();
        this.initializeMonth();
        this.initializeDayOfCurrentMonth();
        this.initializeManDays();
    }

    private initializeYears() {
        for (let i = 2015; i <= this.currentYear; i++) {
            this.years.push(i);
        }
    }

    private initializeMonth() {
        let lastYear = 12;
        this.months = [];
        if (this.appRequestBody.year == this.currentYear) {
            lastYear = this.currentMonth;
        }
        for (let i = 1; i <= lastYear; i++) {
            this.months.push(i);
        }
        this.initializeDayOfCurrentMonth();
    }

    private initializeManDays() {
        for (let i = 1; i <= 40; i++) {
            this.manDays.push(i);
        }
    }

    private initializeDayOfCurrentMonth() {
        this.days = [];
        for (let i = 1; i <= this.getDaysOfMonth(this.appRequestBody.year, this.appRequestBody.month); i++) {
            this.days.push(i);
        }
    }

    private getDaysOfMonth(year, month) {
        return new Date(year, month, 0).getDate();
    }
}
