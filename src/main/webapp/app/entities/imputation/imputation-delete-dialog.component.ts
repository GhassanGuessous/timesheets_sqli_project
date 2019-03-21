import { Component, OnInit, OnDestroy } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';

import { NgbActiveModal, NgbModal, NgbModalRef } from '@ng-bootstrap/ng-bootstrap';
import { JhiEventManager } from 'ng-jhipster';

import { IImputation } from 'app/shared/model/imputation.model';
import { ImputationService } from './imputation.service';

@Component({
    selector: 'jhi-imputation-delete-dialog',
    templateUrl: './imputation-delete-dialog.component.html'
})
export class ImputationDeleteDialogComponent {
    imputation: IImputation;

    constructor(
        protected imputationService: ImputationService,
        public activeModal: NgbActiveModal,
        protected eventManager: JhiEventManager
    ) {}

    clear() {
        this.activeModal.dismiss('cancel');
    }

    confirmDelete(id: number) {
        this.imputationService.delete(id).subscribe(response => {
            this.eventManager.broadcast({
                name: 'imputationListModification',
                content: 'Deleted an imputation'
            });
            this.activeModal.dismiss(true);
        });
    }
}

@Component({
    selector: 'jhi-imputation-delete-popup',
    template: ''
})
export class ImputationDeletePopupComponent implements OnInit, OnDestroy {
    protected ngbModalRef: NgbModalRef;

    constructor(protected activatedRoute: ActivatedRoute, protected router: Router, protected modalService: NgbModal) {}

    ngOnInit() {
        this.activatedRoute.data.subscribe(({ imputation }) => {
            setTimeout(() => {
                this.ngbModalRef = this.modalService.open(ImputationDeleteDialogComponent as Component, { size: 'lg', backdrop: 'static' });
                this.ngbModalRef.componentInstance.imputation = imputation;
                this.ngbModalRef.result.then(
                    result => {
                        this.router.navigate(['/imputation', { outlets: { popup: null } }]);
                        this.ngbModalRef = null;
                    },
                    reason => {
                        this.router.navigate(['/imputation', { outlets: { popup: null } }]);
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
