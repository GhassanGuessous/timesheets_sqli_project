import { Component, OnInit } from '@angular/core';
import { ITeam } from 'app/shared/model/team.model';
import { ComparatorAppPpmcService } from 'app/entities/comparator-app-ppmc/comparator-app-ppmc.service';
import { AccountService } from 'app/core';
import { TeamService } from 'app/entities/team';
import { HttpResponse } from '@angular/common/http';
import { AppRequestBody, IAppRequestBody } from 'app/shared/model/app-request-body';

@Component({
    selector: 'jhi-comparator-app-ppmc',
    templateUrl: './comparator-app-ppmc.component.html',
    styles: []
})
export class ComparatorAppPpmcComponent implements OnInit {
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
        if (this.selectedFiles !== undefined) {
            this.currentFileUpload = this.selectedFiles.item(0);
            this.comparatorAppPpmcService.pushFileToStorage(this.currentFileUpload, this.appRequestBody).subscribe(
                event => {
                    if (event instanceof HttpResponse) {
                        console.log(event.body);
                        this.comparator = event.body;
                    }
                },
                error => {
                    console.log(error);
                }
            );
        }
        this.selectedFiles = undefined;
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
        if (this.appRequestBody.year == this.currentYear) {
            lastYear = this.currentMonth;
        }
        for (let i = 1; i <= lastYear; i++) {
            this.months.push(i);
        }
    }
}
