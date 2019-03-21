import { Component, OnInit, OnDestroy } from '@angular/core';
import { HttpErrorResponse, HttpResponse } from '@angular/common/http';
import { Subscription } from 'rxjs';
import { filter, map } from 'rxjs/operators';
import { JhiEventManager, JhiAlertService } from 'ng-jhipster';

import { IDeliveryCoordinator } from 'app/shared/model/delivery-coordinator.model';
import { AccountService } from 'app/core';
import { DeliveryCoordinatorService } from './delivery-coordinator.service';

@Component({
    selector: 'jhi-delivery-coordinator',
    templateUrl: './delivery-coordinator.component.html'
})
export class DeliveryCoordinatorComponent implements OnInit, OnDestroy {
    deliveryCoordinators: IDeliveryCoordinator[];
    currentAccount: any;
    eventSubscriber: Subscription;

    constructor(
        protected deliveryCoordinatorService: DeliveryCoordinatorService,
        protected jhiAlertService: JhiAlertService,
        protected eventManager: JhiEventManager,
        protected accountService: AccountService
    ) {}

    loadAll() {
        this.deliveryCoordinatorService
            .query()
            .pipe(
                filter((res: HttpResponse<IDeliveryCoordinator[]>) => res.ok),
                map((res: HttpResponse<IDeliveryCoordinator[]>) => res.body)
            )
            .subscribe(
                (res: IDeliveryCoordinator[]) => {
                    this.deliveryCoordinators = res;
                },
                (res: HttpErrorResponse) => this.onError(res.message)
            );
    }

    ngOnInit() {
        this.loadAll();
        this.accountService.identity().then(account => {
            this.currentAccount = account;
        });
        this.registerChangeInDeliveryCoordinators();
    }

    ngOnDestroy() {
        this.eventManager.destroy(this.eventSubscriber);
    }

    trackId(index: number, item: IDeliveryCoordinator) {
        return item.id;
    }

    registerChangeInDeliveryCoordinators() {
        this.eventSubscriber = this.eventManager.subscribe('deliveryCoordinatorListModification', response => this.loadAll());
    }

    protected onError(errorMessage: string) {
        this.jhiAlertService.error(errorMessage, null, null);
    }
}
