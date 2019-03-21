import { Component, OnInit, OnDestroy } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';

import { NgbActiveModal, NgbModal, NgbModalRef } from '@ng-bootstrap/ng-bootstrap';
import { JhiEventManager } from 'ng-jhipster';

import { ICollaboratorMonthlyImputation } from 'app/shared/model/collaborator-monthly-imputation.model';
import { CollaboratorMonthlyImputationService } from './collaborator-monthly-imputation.service';

@Component({
    selector: 'jhi-collaborator-monthly-imputation-delete-dialog',
    templateUrl: './collaborator-monthly-imputation-delete-dialog.component.html'
})
export class CollaboratorMonthlyImputationDeleteDialogComponent {
    collaboratorMonthlyImputation: ICollaboratorMonthlyImputation;

    constructor(
        protected collaboratorMonthlyImputationService: CollaboratorMonthlyImputationService,
        public activeModal: NgbActiveModal,
        protected eventManager: JhiEventManager
    ) {}

    clear() {
        this.activeModal.dismiss('cancel');
    }

    confirmDelete(id: number) {
        this.collaboratorMonthlyImputationService.delete(id).subscribe(response => {
            this.eventManager.broadcast({
                name: 'collaboratorMonthlyImputationListModification',
                content: 'Deleted an collaboratorMonthlyImputation'
            });
            this.activeModal.dismiss(true);
        });
    }
}

@Component({
    selector: 'jhi-collaborator-monthly-imputation-delete-popup',
    template: ''
})
export class CollaboratorMonthlyImputationDeletePopupComponent implements OnInit, OnDestroy {
    protected ngbModalRef: NgbModalRef;

    constructor(protected activatedRoute: ActivatedRoute, protected router: Router, protected modalService: NgbModal) {}

    ngOnInit() {
        this.activatedRoute.data.subscribe(({ collaboratorMonthlyImputation }) => {
            setTimeout(() => {
                this.ngbModalRef = this.modalService.open(CollaboratorMonthlyImputationDeleteDialogComponent as Component, {
                    size: 'lg',
                    backdrop: 'static'
                });
                this.ngbModalRef.componentInstance.collaboratorMonthlyImputation = collaboratorMonthlyImputation;
                this.ngbModalRef.result.then(
                    result => {
                        this.router.navigate(['/collaborator-monthly-imputation', { outlets: { popup: null } }]);
                        this.ngbModalRef = null;
                    },
                    reason => {
                        this.router.navigate(['/collaborator-monthly-imputation', { outlets: { popup: null } }]);
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
