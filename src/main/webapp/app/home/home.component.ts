import { Component, OnInit } from '@angular/core';
import { NgbModalRef } from '@ng-bootstrap/ng-bootstrap';
import { JhiEventManager } from 'ng-jhipster';

import { LoginModalService, AccountService, Account } from 'app/core';
import { TeamService } from 'app/entities/team';

@Component({
    selector: 'jhi-home',
    templateUrl: './home.component.html',
    styleUrls: ['home.css']
})
export class HomeComponent implements OnInit {
    account: Account;
    myTeam: any;
    modalRef: NgbModalRef;

    constructor(
        private accountService: AccountService,
        protected teamService: TeamService,
        private loginModalService: LoginModalService,
        private eventManager: JhiEventManager
    ) {}

    ngOnInit() {
        this.accountService.identity().then((account: Account) => {
            this.account = account;
            if (this.isDelco()) {
                this.loadDelcoTeam();
            }
        });
        this.registerAuthenticationSuccess();
    }

    loadDelcoTeam() {
        this.teamService.findByDelco(this.account.id).subscribe(data => {
            this.myTeam = data.body;
        });
    }

    registerAuthenticationSuccess() {
        this.eventManager.subscribe('authenticationSuccess', message => {
            this.accountService.identity().then(account => {
                this.account = account;
            });
        });
    }

    isAuthenticated() {
        return this.accountService.isAuthenticated();
    }

    private isDelco() {
        return this.account && this.account.authorities.includes('ROLE_DELCO');
    }

    login() {
        this.modalRef = this.loginModalService.open();
    }
}
