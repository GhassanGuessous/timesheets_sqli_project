import { Component, OnInit, OnDestroy } from '@angular/core';
import { HttpErrorResponse, HttpResponse } from '@angular/common/http';
import { Subscription } from 'rxjs';
import { filter, map } from 'rxjs/operators';
import { JhiEventManager, JhiAlertService } from 'ng-jhipster';

import { IAdministrator } from 'app/shared/model/administrator.model';
import { AccountService } from 'app/core';
import { AdministratorService } from './administrator.service';

@Component({
    selector: 'jhi-administrator',
    templateUrl: './administrator.component.html'
})
export class AdministratorComponent implements OnInit, OnDestroy {
    administrators: IAdministrator[];
    currentAccount: any;
    eventSubscriber: Subscription;

    constructor(
        protected administratorService: AdministratorService,
        protected jhiAlertService: JhiAlertService,
        protected eventManager: JhiEventManager,
        protected accountService: AccountService
    ) {}

    loadAll() {
        this.administratorService
            .query()
            .pipe(
                filter((res: HttpResponse<IAdministrator[]>) => res.ok),
                map((res: HttpResponse<IAdministrator[]>) => res.body)
            )
            .subscribe(
                (res: IAdministrator[]) => {
                    this.administrators = res;
                },
                (res: HttpErrorResponse) => this.onError(res.message)
            );
    }

    ngOnInit() {
        this.loadAll();
        this.accountService.identity().then(account => {
            this.currentAccount = account;
        });
        this.registerChangeInAdministrators();
    }

    ngOnDestroy() {
        this.eventManager.destroy(this.eventSubscriber);
    }

    trackId(index: number, item: IAdministrator) {
        return item.id;
    }

    registerChangeInAdministrators() {
        this.eventSubscriber = this.eventManager.subscribe('administratorListModification', response => this.loadAll());
    }

    protected onError(errorMessage: string) {
        this.jhiAlertService.error(errorMessage, null, null);
    }
}
