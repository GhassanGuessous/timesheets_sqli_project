import { Component, OnInit, OnDestroy } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';

import { NgbActiveModal, NgbModal, NgbModalRef } from '@ng-bootstrap/ng-bootstrap';
import { JhiEventManager } from 'ng-jhipster';

import { ICollaboratorDailyImputation } from 'app/shared/model/collaborator-daily-imputation.model';
import { CollaboratorDailyImputationService } from './collaborator-daily-imputation.service';

@Component({
    selector: 'jhi-collaborator-daily-imputation-delete-dialog',
    templateUrl: './collaborator-daily-imputation-delete-dialog.component.html'
})
export class CollaboratorDailyImputationDeleteDialogComponent {
    collaboratorDailyImputation: ICollaboratorDailyImputation;

    constructor(
        protected collaboratorDailyImputationService: CollaboratorDailyImputationService,
        public activeModal: NgbActiveModal,
        protected eventManager: JhiEventManager
    ) {}

    clear() {
        this.activeModal.dismiss('cancel');
    }

    confirmDelete(id: number) {
        this.collaboratorDailyImputationService.delete(id).subscribe(response => {
            this.eventManager.broadcast({
                name: 'collaboratorDailyImputationListModification',
                content: 'Deleted an collaboratorDailyImputation'
            });
            this.activeModal.dismiss(true);
        });
    }
}

@Component({
    selector: 'jhi-collaborator-daily-imputation-delete-popup',
    template: ''
})
export class CollaboratorDailyImputationDeletePopupComponent implements OnInit, OnDestroy {
    protected ngbModalRef: NgbModalRef;

    constructor(protected activatedRoute: ActivatedRoute, protected router: Router, protected modalService: NgbModal) {}

    ngOnInit() {
        this.activatedRoute.data.subscribe(({ collaboratorDailyImputation }) => {
            setTimeout(() => {
                this.ngbModalRef = this.modalService.open(CollaboratorDailyImputationDeleteDialogComponent as Component, {
                    size: 'lg',
                    backdrop: 'static'
                });
                this.ngbModalRef.componentInstance.collaboratorDailyImputation = collaboratorDailyImputation;
                this.ngbModalRef.result.then(
                    result => {
                        this.router.navigate(['/collaborator-daily-imputation', { outlets: { popup: null } }]);
                        this.ngbModalRef = null;
                    },
                    reason => {
                        this.router.navigate(['/collaborator-daily-imputation', { outlets: { popup: null } }]);
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
