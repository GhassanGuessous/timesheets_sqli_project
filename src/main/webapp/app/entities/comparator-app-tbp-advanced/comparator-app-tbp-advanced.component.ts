import { Component, OnInit } from '@angular/core';
import { ITeam } from 'app/shared/model/team.model';
import { AccountService } from 'app/core';
import { TeamService } from 'app/entities/team';
import { ComparatorAppTbpAdvancedService } from 'app/entities/comparator-app-tbp-advanced/comparator-app-tbp-advanced.service';
import { AppTbpRequestBody } from 'app/shared/model/app-tbp-request-body';

@Component({
    selector: 'jhi-comparator-app-tbp-advanced',
    templateUrl: './comparator-app-tbp-advanced.component.html',
    styles: []
})
export class ComparatorAppTbpAdvancedComponent implements OnInit {
    private currentAccount: any;
    private myTeam: ITeam;
    private allTeams: Array<ITeam>;
    private currentYear: number = new Date().getFullYear();
    private currentMonth: number = new Date().getMonth() + 1;
    private years: Array<number> = [];
    private months: Array<number>;
    private predicate: any;
    private reverse: any;
    private comparator: any;
    private imputationDays: Array<number>;
    private appTbpRequestBody: AppTbpRequestBody = new AppTbpRequestBody(null, this.currentYear, this.currentMonth);

    constructor(
        protected service: ComparatorAppTbpAdvancedService,
        protected accountService: AccountService,
        protected teamService: TeamService
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
        for (let i = 2015; i <= this.currentYear; i++) {
            this.years.push(i);
        }
    }

    private initializeMonth() {
        let lastYear = 12;
        this.months = [];
        if (this.appTbpRequestBody.year == this.currentYear) {
            lastYear = this.currentMonth;
        }
        for (let i = 1; i <= lastYear; i++) {
            this.months.push(i);
        }
    }

    compare() {
        console.log(this.appTbpRequestBody);
        this.service.compare(this.appTbpRequestBody).subscribe(res => {
            this.comparator = res.body;
            this.initializeDays();
        });
    }
    private initializeDays() {
        this.imputationDays = [];
        this.addDaysFromMonthlyImputation(this.comparator.appMonthlyImputation);
        this.addDaysFromMonthlyImputation(this.comparator.comparerdMonthlyImputation);
        this.removeDuplecates();
    }

    private addDaysFromMonthlyImputation(monthly) {
        monthly.dailyImputations.forEach(daily => {
            this.imputationDays.push(daily.day);
        });
    }

    private removeDuplecates() {
        this.imputationDays = Array.from(new Set(this.imputationDays)).sort((a, b) => a - b);
    }
}
