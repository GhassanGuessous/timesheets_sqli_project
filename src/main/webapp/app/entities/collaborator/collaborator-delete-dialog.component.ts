import { Component, OnInit, OnDestroy } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';

import { NgbActiveModal, NgbModal, NgbModalRef } from '@ng-bootstrap/ng-bootstrap';
import { JhiEventManager } from 'ng-jhipster';

import { ICollaborator } from 'app/shared/model/collaborator.model';
import { CollaboratorService } from './collaborator.service';

@Component({
    selector: 'jhi-collaborator-delete-dialog',
    templateUrl: './collaborator-delete-dialog.component.html'
})
export class CollaboratorDeleteDialogComponent {
    collaborator: ICollaborator;

    constructor(
        protected collaboratorService: CollaboratorService,
        public activeModal: NgbActiveModal,
        protected eventManager: JhiEventManager
    ) {}

    clear() {
        this.activeModal.dismiss('cancel');
    }

    confirmDelete(id: number) {
        this.collaboratorService.delete(id).subscribe(response => {
            this.eventManager.broadcast({
                name: 'collaboratorListModification',
                content: 'Deleted an collaborator'
            });
            this.activeModal.dismiss(true);
        });
    }
}

@Component({
    selector: 'jhi-collaborator-delete-popup',
    template: ''
})
export class CollaboratorDeletePopupComponent implements OnInit, OnDestroy {
    protected ngbModalRef: NgbModalRef;

    constructor(protected activatedRoute: ActivatedRoute, protected router: Router, protected modalService: NgbModal) {}

    ngOnInit() {
        this.activatedRoute.data.subscribe(({ collaborator }) => {
            setTimeout(() => {
                this.ngbModalRef = this.modalService.open(CollaboratorDeleteDialogComponent as Component, {
                    size: 'lg',
                    backdrop: 'static'
                });
                this.ngbModalRef.componentInstance.collaborator = collaborator;
                this.ngbModalRef.result.then(
                    result => {
                        this.router.navigate(['/collaborator', { outlets: { popup: null } }]);
                        this.ngbModalRef = null;
                    },
                    reason => {
                        this.router.navigate(['/collaborator', { outlets: { popup: null } }]);
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
