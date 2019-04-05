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
    currentAccount: any;
    myTeam: ITeam;
    allTeams: ITeam[];
    appRequestBody: IAppRequestBody = new AppRequestBody();
    predicate: any;
    reverse: any;
    imputations: any;
    private days: Array<number> = [];

    constructor(protected service: TimesheetAppService, protected accountService: AccountService, protected teamService: TeamService) {}

    ngOnInit() {
        console.log(new Date(2019, 2, 0).getDate());
        this.accountService.identity().then(account => {
            this.currentAccount = account;
            // if (this.isAdmin()) {
            this.loadAll();
            // } else {
            this.loadDelcoTeam(account.id);
            //   }
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
            this.initDays(res.body);
        });
    }

    private initDays(res: IImputation) {
        res.monthlyImputations[res.monthlyImputations.length - 1].dailyImputations.forEach(item => {
            return this.days.push(item.day);
        });
    }
}
