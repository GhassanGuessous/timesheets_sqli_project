import { Component, OnInit, OnDestroy } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';

import { NgbActiveModal, NgbModal, NgbModalRef } from '@ng-bootstrap/ng-bootstrap';
import { JhiEventManager } from 'ng-jhipster';

import { IImputationType } from 'app/shared/model/imputation-type.model';
import { ImputationTypeService } from './imputation-type.service';

@Component({
    selector: 'jhi-imputation-type-delete-dialog',
    templateUrl: './imputation-type-delete-dialog.component.html'
})
export class ImputationTypeDeleteDialogComponent {
    imputationType: IImputationType;

    constructor(
        protected imputationTypeService: ImputationTypeService,
        public activeModal: NgbActiveModal,
        protected eventManager: JhiEventManager
    ) {}

    clear() {
        this.activeModal.dismiss('cancel');
    }

    confirmDelete(id: number) {
        this.imputationTypeService.delete(id).subscribe(response => {
            this.eventManager.broadcast({
                name: 'imputationTypeListModification',
                content: 'Deleted an imputationType'
            });
            this.activeModal.dismiss(true);
        });
    }
}

@Component({
    selector: 'jhi-imputation-type-delete-popup',
    template: ''
})
export class ImputationTypeDeletePopupComponent implements OnInit, OnDestroy {
    protected ngbModalRef: NgbModalRef;

    constructor(protected activatedRoute: ActivatedRoute, protected router: Router, protected modalService: NgbModal) {}

    ngOnInit() {
        this.activatedRoute.data.subscribe(({ imputationType }) => {
            setTimeout(() => {
                this.ngbModalRef = this.modalService.open(ImputationTypeDeleteDialogComponent as Component, {
                    size: 'lg',
                    backdrop: 'static'
                });
                this.ngbModalRef.componentInstance.imputationType = imputationType;
                this.ngbModalRef.result.then(
                    result => {
                        this.router.navigate(['/imputation-type', { outlets: { popup: null } }]);
                        this.ngbModalRef = null;
                    },
                    reason => {
                        this.router.navigate(['/imputation-type', { outlets: { popup: null } }]);
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
