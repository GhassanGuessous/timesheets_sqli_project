import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { HttpResponse, HttpErrorResponse } from '@angular/common/http';
import { Observable } from 'rxjs';
import { filter, map } from 'rxjs/operators';
import { IDeliveryCoordinator } from 'app/shared/model/delivery-coordinator.model';
import { DeliveryCoordinatorService } from './delivery-coordinator.service';

@Component({
    selector: 'jhi-delivery-coordinator-update',
    templateUrl: './delivery-coordinator-update.component.html'
})
export class DeliveryCoordinatorUpdateComponent implements OnInit {
    deliveryCoordinator: IDeliveryCoordinator;
    isSaving: boolean;

    constructor(protected deliveryCoordinatorService: DeliveryCoordinatorService, protected activatedRoute: ActivatedRoute) {}

    ngOnInit() {
        this.isSaving = false;
        this.activatedRoute.data.subscribe(({ deliveryCoordinator }) => {
            this.deliveryCoordinator = deliveryCoordinator;
        });
    }

    previousState() {
        window.history.back();
    }

    save() {
        this.isSaving = true;
        if (this.deliveryCoordinator.id !== undefined) {
            this.subscribeToSaveResponse(this.deliveryCoordinatorService.update(this.deliveryCoordinator));
        } else {
            this.subscribeToSaveResponse(this.deliveryCoordinatorService.create(this.deliveryCoordinator));
        }
    }

    protected subscribeToSaveResponse(result: Observable<HttpResponse<IDeliveryCoordinator>>) {
        result.subscribe((res: HttpResponse<IDeliveryCoordinator>) => this.onSaveSuccess(), (res: HttpErrorResponse) => this.onSaveError());
    }

    protected onSaveSuccess() {
        this.isSaving = false;
        this.previousState();
    }

    protected onSaveError() {
        this.isSaving = false;
    }
}
