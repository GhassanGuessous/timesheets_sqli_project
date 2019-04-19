import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ITeam } from 'app/shared/model/team.model';
import { AppRequestBody, IAppRequestBody } from 'app/shared/model/app-request-body';
import { ComparatorAppPpmcAdvancedService } from 'app/entities/comparator-app-ppmc-advanced/comparator-app-ppmc-advanced.service';
import { AccountService } from 'app/core';
import { TeamService } from 'app/entities/team';

@Component({
    selector: 'jhi-comparator-app-ppmc-advanced',
    templateUrl: './comparator-app-ppmc-advanced.component.html',
    styles: []
})
export class ComparatorAppPpmcAdvancedComponent implements OnInit {
    private selectedFiles: FileList;
    private currentFileUpload: File;
    private comparator?: any;
    private currentAccount: any;
    private myTeam: ITeam;
    private allTeams: ITeam[];
    private currentYear: number = new Date().getFullYear();
    private currentMonth: number = new Date().getMonth() + 1;
    private years: Array<number> = [];
    private months: Array<number> = [];
    private imputationDays: Array<number>;
    private numberOfDaysOfCurrentMonth: number = new Date(this.currentYear, this.currentMonth, 0).getDate();
    private appRequestBody: IAppRequestBody = new AppRequestBody(
        '',
        this.currentYear,
        this.currentMonth,
        1,
        this.numberOfDaysOfCurrentMonth
    );

    constructor(
        protected accountService: AccountService,
        protected teamService: TeamService,
        protected comparatorAppPpmcAdvancedService: ComparatorAppPpmcAdvancedService
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

    private selectFile(event) {
        this.selectedFiles = event.target.files;
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

    private compare() {
        if (this.selectedFiles !== undefined) {
            this.currentFileUpload = this.selectedFiles.item(0);
            this.comparatorAppPpmcAdvancedService.getAdvancedComparison(this.currentFileUpload, this.appRequestBody).subscribe(
                event => {
                    if (event instanceof HttpResponse) {
                        this.comparator = event.body;
                        this.initializeDays();
                    }
                },
                error => {
                    console.log(error);
                }
            );
        }
        this.selectedFiles = undefined;
    }

    private getColor(element: any, day: number): string {
        if (element.appMonthlyImputation && element.comparedMonthlyImputation) {
            let appDaily = this.findDailyImputation(element.appMonthlyImputation, day);
            let comparedDaily = this.findDailyImputation(element.comparedMonthlyImputation, day);
            if (appDaily && comparedDaily) {
                return this.getColorWhenDifferentCharge(appDaily, comparedDaily);
            } else {
                if (this.isOneUndefined(appDaily, comparedDaily)) {
                    return this.getDefinedOne(appDaily, comparedDaily).charge == 0 ? '' : '#feabab';
                }
            }
            return '';
        }
    }

    private findDailyImputation(monthlyImputation: any, day: number): any {
        let appDaily = monthlyImputation.dailyImputations.find(daily => daily.day === day);
        return appDaily;
    }

    private getColorWhenDifferentCharge(appDaily, comparedDaily): string {
        if (appDaily.charge !== comparedDaily.charge) {
            return '#feabab';
        }
    }

    private isOneUndefined(appDaily, ppmcDaily): boolean {
        return (!appDaily && ppmcDaily) || (appDaily && !ppmcDaily);
    }

    private getDefinedOne(appDaily, comparedDaily) {
        return appDaily ? appDaily : comparedDaily;
    }

    private isAdmin() {
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
        if (this.appRequestBody.year == this.currentYear) {
            lastYear = this.currentMonth;
        }
        for (let i = 1; i <= lastYear; i++) {
            this.months.push(i);
        }
    }

    private initializeDays() {
        this.imputationDays = [];
        this.addDaysFromMonthlyImputation();
        this.removeDuplecates();
    }

    private addDaysFromMonthlyImputation() {
        this.comparator.forEach(element => {
            if (element.appMonthlyImputation) {
                element.appMonthlyImputation.dailyImputations.forEach(daily => {
                    this.imputationDays.push(daily.day);
                });
                element.comparedMonthlyImputation.dailyImputations.forEach(daily => {
                    this.imputationDays.push(daily.day);
                });
            }
        });
    }

    private removeDuplecates() {
        this.imputationDays = Array.from(new Set(this.imputationDays)).sort((a, b) => a - b);
    }
}
