import { Component, OnInit } from '@angular/core';
import { TimesheetPpmcService } from '../timesheet-ppmc/timesheet-ppmc.service';
import { HttpResponse, HttpEventType } from '@angular/common/http';
import { IImputation } from 'app/shared/model/imputation.model';
import { AccountService } from 'app/core';
import { TeamService } from 'app/entities/team';
import { ITeam } from 'app/shared/model/team.model';
import { AppRequestBody, IAppRequestBody } from 'app/shared/model/app-request-body';

@Component({
    selector: 'jhi-timesheet-ppmc',
    templateUrl: './timesheet-ppmc.component.html',
    styles: []
})
export class TimesheetPpmcComponent implements OnInit {
    selectedFiles: FileList;
    currentFileUpload: File;
    private isNewUpload? = true;
    private currentAccount: any;
    imputation: IImputation;
    private days: Array<number> = [];
    private myTeam: ITeam;
    private allTeams: ITeam[];
    private years: Array<number> = [];
    private months: Array<number> = [];
    progress: { percentage: number } = { percentage: 0 };
    private currentYear: number = new Date().getFullYear();
    private currentMonth: number = new Date().getMonth() + 1;
    private appRequestBody: IAppRequestBody = new AppRequestBody('', this.currentYear, this.currentMonth, 1, 0);

    constructor(
        protected accountService: AccountService,
        private timesheetPpmcService: TimesheetPpmcService,
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

    selectFile(event) {
        this.selectedFiles = event.target.files;
    }

    upload() {
        if (this.selectedFiles !== undefined) {
            this.progress.percentage = 0;
            this.currentFileUpload = this.selectedFiles.item(0);
            this.timesheetPpmcService.getPpmcTimeSheet(this.currentFileUpload, this.appRequestBody).subscribe(event => {
                if (event.type === HttpEventType.UploadProgress) {
                    this.progress.percentage = Math.round((100 * event.loaded) / event.total);
                } else if (event instanceof HttpResponse) {
                    this.imputation = event.body;
                    this.initializeDays(this.imputation);
                }
            });
        } else {
            this.timesheetPpmcService.getPpmcTimeSheetFromDB(this.appRequestBody).subscribe(res => {
                this.imputation = res.body;
                this.initializeDays(this.imputation);
            });
        }
        this.selectedFiles = undefined;
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
        if (this.appRequestBody.year == this.currentYear) {
            lastMonthInYear = this.currentMonth;
        }
        for (let i = 1; i <= lastMonthInYear; i++) {
            this.months.push(i);
        }
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

    private isAdmin() {
        return this.currentAccount.authorities.includes('ROLE_ADMIN');
    }

    private setIsNewUpload(predicate: string) {
        this.isNewUpload = predicate === 'false' ? false : true;
    }
}
