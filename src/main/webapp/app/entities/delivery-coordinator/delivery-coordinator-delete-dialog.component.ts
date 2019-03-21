import { Component, OnInit, OnDestroy } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';

import { NgbActiveModal, NgbModal, NgbModalRef } from '@ng-bootstrap/ng-bootstrap';
import { JhiEventManager } from 'ng-jhipster';

import { IDeliveryCoordinator } from 'app/shared/model/delivery-coordinator.model';
import { DeliveryCoordinatorService } from './delivery-coordinator.service';

@Component({
    selector: 'jhi-delivery-coordinator-delete-dialog',
    templateUrl: './delivery-coordinator-delete-dialog.component.html'
})
export class DeliveryCoordinatorDeleteDialogComponent {
    deliveryCoordinator: IDeliveryCoordinator;

    constructor(
        protected deliveryCoordinatorService: DeliveryCoordinatorService,
        public activeModal: NgbActiveModal,
        protected eventManager: JhiEventManager
    ) {}

    clear() {
        this.activeModal.dismiss('cancel');
    }

    confirmDelete(id: number) {
        this.deliveryCoordinatorService.delete(id).subscribe(response => {
            this.eventManager.broadcast({
                name: 'deliveryCoordinatorListModification',
                content: 'Deleted an deliveryCoordinator'
            });
            this.activeModal.dismiss(true);
        });
    }
}

@Component({
    selector: 'jhi-delivery-coordinator-delete-popup',
    template: ''
})
export class DeliveryCoordinatorDeletePopupComponent implements OnInit, OnDestroy {
    protected ngbModalRef: NgbModalRef;

    constructor(protected activatedRoute: ActivatedRoute, protected router: Router, protected modalService: NgbModal) {}

    ngOnInit() {
        this.activatedRoute.data.subscribe(({ deliveryCoordinator }) => {
            setTimeout(() => {
                this.ngbModalRef = this.modalService.open(DeliveryCoordinatorDeleteDialogComponent as Component, {
                    size: 'lg',
                    backdrop: 'static'
                });
                this.ngbModalRef.componentInstance.deliveryCoordinator = deliveryCoordinator;
                this.ngbModalRef.result.then(
                    result => {
                        this.router.navigate(['/delivery-coordinator', { outlets: { popup: null } }]);
                        this.ngbModalRef = null;
                    },
                    reason => {
                        this.router.navigate(['/delivery-coordinator', { outlets: { popup: null } }]);
                        this.ngbModalRef = null;
                    }
                );
            }, 0);
        });
    }

    ngOnDestroy() {
        this.ngbModalRef = null;
    }
}
