import { Component, OnInit } from '@angular/core';
import { ITeam } from 'app/shared/model/team.model';
import { AccountService } from 'app/core';
import { TeamService } from 'app/entities/team';
import { ComparatorAPPTBPService } from 'app/entities/comparator-app-tbp/comparator-app-tbp.service';
import { AppTbpRequestBody, IAppTbpRequestBody } from 'app/shared/model/app-tbp-request-body';
import { AuthTbpModalService } from 'app/core/authTbp/auth-tbp-modal.service';

@Component({
    selector: 'jhi-comparator-app-tbp',
    templateUrl: './comparator-app-tbp.component.html',
    styles: []
})
export class ComparatorAPPTBPComponent implements OnInit {
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
    private appTbpRequestBody: IAppTbpRequestBody = new AppTbpRequestBody(null, this.currentYear, this.currentMonth);

    constructor(
        protected service: ComparatorAPPTBPService,
        protected accountService: AccountService,
        protected teamService: TeamService,
        private authTbpModalService: AuthTbpModalService
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
        const startImputationsYear = 2015;
        for (let i = startImputationsYear; i <= this.currentYear; i++) {
            this.years.push(i);
        }
    }

    private initializeMonth() {
        let lastMonthInYear = 12;
        this.months = [];
        if (this.appTbpRequestBody.year == this.currentYear) {
            lastMonthInYear = this.currentMonth;
        }
        for (let i = 1; i <= lastMonthInYear; i++) {
            this.months.push(i);
        }
    }

    compare() {
        console.log(this.appTbpRequestBody);
        if (localStorage.getItem('isTbpAuthenticated') === 'false') {
            this.authenticateThenCompare();
        } else {
            this.compareWhenIsAlreadyAuthenticated();
        }
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

    private authenticateThenCompare() {
        this.appTbpRequestBody.requestType = 'APP_TBP_COMPARATOR';
        this.authTbpModalService.open(this.appTbpRequestBody).then(
            result => {
                this.comparator = result;
            },
            reason => {
                console.log(reason);
            }
        );
    }

    private compareWhenIsAlreadyAuthenticated() {
        this.appTbpRequestBody.username = localStorage.getItem('username');
        this.appTbpRequestBody.password = localStorage.getItem('password');
        this.service.compare(this.appTbpRequestBody).subscribe(res => {
            this.comparator = res.body;
        });
    }
}
