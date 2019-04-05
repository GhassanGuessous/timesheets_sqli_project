import { Component, OnInit } from '@angular/core';
import { AccountService } from 'app/core';
import { HttpErrorResponse, HttpResponse } from '@angular/common/http';
import { ITeam } from 'app/shared/model/team.model';
import { ITbpRequestBody, TbpRequestBody } from 'app/shared/model/tbp-request-body';
import { TimesheetTbpService } from 'app/entities/timesheet-tbp/timesheet-tbp.service';
import { IImputation } from 'app/shared/model/imputation.model';
import { TeamService } from 'app/entities/team';

@Component({
    selector: 'jhi-timesheet-tbp',
    templateUrl: './timesheet-tbp.component.html',
    styles: []
})
export class TimesheetTbpComponent implements OnInit {
    currentAccount: any;
    tbpRequestBody: ITbpRequestBody = new TbpRequestBody();
    myTeam: ITeam;
    allTeams: ITeam[];
    imputation: IImputation;
    predicate: any;
    reverse: any;
    constructor(
        protected accountService: AccountService,
        protected teamService: TeamService,
        protected timesheetTbpService: TimesheetTbpService
    ) {}

    ngOnInit() {
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

    getTimesheet() {
        console.log('request body', this.tbpRequestBody);
        this.timesheetTbpService.findTbpChargeByTeam(this.tbpRequestBody).subscribe(
            res => {
                this.imputation = res.body;
                console.log(this.imputation);
            },
            error => {
                console.log(error);
            }
        );
    }
}
