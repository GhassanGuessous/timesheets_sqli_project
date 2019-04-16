import { Component, OnInit } from '@angular/core';
import { ITeam } from 'app/shared/model/team.model';
import { AccountService } from 'app/core';
import { TeamService } from 'app/entities/team';
import { ComparatorAPPTBPService } from 'app/entities/comparator-app-tbp/comparator-app-tbp.service';
import { AppTbpRequestBody } from 'app/shared/model/app-tbp-request.body';

@Component({
    selector: 'jhi-comparator-app-tbp',
    templateUrl: './comparator-app-tbp.component.html',
    styles: []
})
export class ComparatorAPPTBPComponent implements OnInit {
    private currentAccount: any;
    private myTeam: ITeam;
    private allTeams: ITeam[];
    private currentYear: number = new Date().getFullYear();
    private currentMonth: number = new Date().getMonth();
    private years: Array<number> = [];
    private months: Array<number>;
    private predicate: any;
    private reverse: any;
    private comparator: any;
    private appTbpRequestBody: AppTbpRequestBody = new AppTbpRequestBody(null, this.currentYear, this.currentMonth);

    constructor(protected service: ComparatorAPPTBPService, protected accountService: AccountService, protected teamService: TeamService) {}

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
        this.service.compare(this.appTbpRequestBody).subscribe(
            res => {
                this.comparator = res.body;
            },
            error => {}
        );
    }

    getcolor(difference: number): string {
        if (difference < 0) {
            return 'red';
        } else if (difference > 0) {
            return 'orange';
        } else {
            return 'green';
        }
    }
}
