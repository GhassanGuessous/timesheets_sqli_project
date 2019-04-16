import { Component, OnInit } from '@angular/core';
import { ITeam } from 'app/shared/model/team.model';
import { ComparatorAppPpmcService } from 'app/entities/comparator-app-ppmc/comparator-app-ppmc.service';
import { AccountService } from 'app/core';
import { TeamService } from 'app/entities/team';
import { IImputation } from 'app/shared/model/imputation.model';
import { HttpEventType, HttpResponse } from '@angular/common/http';
import { AppRequestBody, IAppRequestBody } from 'app/shared/model/app-request-body';
import { AppPpmcComparator, IAppPpmcComparator } from 'app/shared/model/app_ppmc_comparator.model';
import { ICollaboratorMonthlyImputation } from 'app/shared/model/collaborator-monthly-imputation.model';

@Component({
    selector: 'jhi-comparator-app-ppmc',
    templateUrl: './comparator-app-ppmc.component.html',
    styles: []
})
export class ComparatorAppPpmcComponent implements OnInit {
    private selectedFiles: FileList;
    private currentFileUpload: File;
    private app_ppmc_map: Map<string, IImputation>;
    private comparator?: Array<IAppPpmcComparator> = [];
    private progress: { percentage: number } = { percentage: 0 };
    private currentAccount: any;
    private myTeam: ITeam;
    private allTeams: ITeam[];
    private currentYear: number = new Date().getFullYear();
    private currentMonth: number = new Date().getMonth();
    private years: Array<number> = [];
    private months: Array<number> = [];
    private numberOfDaysOfCurrentMonth: number = new Date(this.currentYear, this.currentMonth, 0).getDate();
    private appRequestBody: IAppRequestBody = new AppRequestBody(
        '',
        this.currentYear,
        this.currentMonth,
        1,
        this.numberOfDaysOfCurrentMonth
    );

    constructor(
        protected service: ComparatorAppPpmcService,
        protected accountService: AccountService,
        protected teamService: TeamService,
        protected comparatorAppPpmcService: ComparatorAppPpmcService
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

    selectFile(event) {
        this.selectedFiles = event.target.files;
    }

    loadAll() {
        this.teamService.findAllTeamsWithoutPagination().subscribe(res => {
            this.allTeams = res.body;
        });
    }

    loadDelcoTeam(id: bigint) {
        this.teamService.findByDelco(id).subscribe(data => {
            this.myTeam = data.body;
            this.appRequestBody.agresso = this.myTeam.agresso;
        });
    }

    compare() {
        this.progress.percentage = 0;
        this.currentFileUpload = this.selectedFiles.item(0);
        this.comparatorAppPpmcService.pushFileToStorage(this.currentFileUpload, this.appRequestBody).subscribe(
            event => {
                if (event.type === HttpEventType.UploadProgress) {
                    this.progress.percentage = Math.round((100 * event.loaded) / event.total);
                } else if (event instanceof HttpResponse) {
                    this.fill_app_ppmc_map(event);
                    this.getComparator();
                }
            },
            error => {
                console.log(error);
            }
        );
        this.selectedFiles = undefined;
    }

    fill_app_ppmc_map(response: any) {
        this.app_ppmc_map = new Map<string, IImputation>();
        this.app_ppmc_map.set('app', response.body['app']);
        this.app_ppmc_map.set('ppmc', response.body['ppmc']);
    }

    getComparator() {
        this.initComarator();
        this.fill_with_app_imputations();
        this.fill_with_ppmc_imputations();
        this.setDifference();
    }

    private initComarator() {
        this.comparator = [];
    }

    fill_with_app_imputations() {
        this.app_ppmc_map.get('app').monthlyImputations.forEach(monthly => {
            this.comparator.push(new AppPpmcComparator(monthly.collaborator, monthly.total, 0, 0));
        });
    }

    fill_with_ppmc_imputations() {
        this.app_ppmc_map.get('ppmc').monthlyImputations.forEach(monthly => {
            if (this.isCollaboratorExist(monthly.collaborator.id)) {
                this.setPPMCTotal(monthly);
            } else {
                this.comparator.push(new AppPpmcComparator(monthly.collaborator, 0, monthly.total, 0));
            }
        });
    }

    private isCollaboratorExist(id: number): boolean {
        return this.comparator.find(element => element.collaborator.id === id) !== undefined;
    }

    private setPPMCTotal(monthly: ICollaboratorMonthlyImputation) {
        this.comparator.find(element => element.collaborator.id === monthly.collaborator.id).totalPpmc = monthly.total;
    }

    private setDifference() {
        this.comparator.forEach(element => {
            element.difference = element.totalApp - element.totalPpmc;
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
        for (let i = 1; i <= 12; i++) {
            this.months.push(i);
        }
    }
}
