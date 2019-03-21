import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';

import { IDeliveryCoordinator } from 'app/shared/model/delivery-coordinator.model';

@Component({
    selector: 'jhi-delivery-coordinator-detail',
    templateUrl: './delivery-coordinator-detail.component.html'
})
export class DeliveryCoordinatorDetailComponent implements OnInit {
    deliveryCoordinator: IDeliveryCoordinator;

    constructor(protected activatedRoute: ActivatedRoute) {}

    ngOnInit() {
        this.activatedRoute.data.subscribe(({ deliveryCoordinator }) => {
            this.deliveryCoordinator = deliveryCoordinator;
        });
    }

    previousState() {
        window.history.back();
    }
}
