import { Component, OnInit } from '@angular/core';
import { AccountService } from 'app/core';
import { HttpErrorResponse, HttpResponse } from '@angular/common/http';
import { ITeam } from 'app/shared/model/team.model';
import { TeamService } from 'app/entities/team';

@Component({
    selector: 'jhi-timesheet-tbp',
    templateUrl: './timesheet-tbp.component.html',
    styles: []
})
export class TimesheetTbpComponent implements OnInit {
    currentAccount: any;
    myTeam: ITeam;
    allTeams: ITeam[];
    currentYear: number = new Date().getFullYear();
    years: Array<number> = [];
    months: Array<number> = [];
    days: Array<number> = [];
    manDays: Array<number> = [];
    predicate: any;
    reverse: any;
    constructor(protected accountService: AccountService, protected teamService: TeamService) {}

    ngOnInit() {
        this.initialize();
        this.accountService.identity().then(account => {
            this.currentAccount = account;
            this.loadAll();
            this.loadDelcoTeam(account.id);
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

    private initialize() {
        this.initializeYears();
        this.initializeMonth();
        this.initializeDays();
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

    private initializeDays() {
        for (let i = 1; i <= 31; i++) {
            this.days.push(i);
        }
    }

    private initializeManDays() {
        for (let i = 1; i <= 40; i++) {
            this.manDays.push(i);
        }
    }

    getTimesheet() {}
}
