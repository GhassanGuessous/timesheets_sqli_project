import { Component, OnInit, OnDestroy } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';

import { NgbActiveModal, NgbModal, NgbModalRef } from '@ng-bootstrap/ng-bootstrap';
import { JhiEventManager } from 'ng-jhipster';

import { ICorrespondence } from 'app/shared/model/correspondence.model';
import { CorrespondenceService } from './correspondence.service';

@Component({
    selector: 'jhi-correspondence-delete-dialog',
    templateUrl: './correspondence-delete-dialog.component.html'
})
export class CorrespondenceDeleteDialogComponent {
    correspondence: ICorrespondence;

    constructor(
        protected correspondenceService: CorrespondenceService,
        public activeModal: NgbActiveModal,
        protected eventManager: JhiEventManager
    ) {}

    clear() {
        this.activeModal.dismiss('cancel');
    }

    confirmDelete(id: number) {
        this.correspondenceService.delete(id).subscribe(response => {
            this.eventManager.broadcast({
                name: 'correspondenceListModification',
                content: 'Deleted an correspondence'
            });
            this.activeModal.dismiss(true);
        });
    }
}

@Component({
    selector: 'jhi-correspondence-delete-popup',
    template: ''
})
export class CorrespondenceDeletePopupComponent implements OnInit, OnDestroy {
    protected ngbModalRef: NgbModalRef;

    constructor(protected activatedRoute: ActivatedRoute, protected router: Router, protected modalService: NgbModal) {}

    ngOnInit() {
        this.activatedRoute.data.subscribe(({ correspondence }) => {
            setTimeout(() => {
                this.ngbModalRef = this.modalService.open(CorrespondenceDeleteDialogComponent as Component, {
                    size: 'lg',
                    backdrop: 'static'
                });
                this.ngbModalRef.componentInstance.correspondence = correspondence;
                this.ngbModalRef.result.then(
                    result => {
                        this.router.navigate(['/correspondence', { outlets: { popup: null } }]);
                        this.ngbModalRef = null;
                    },
                    reason => {
                        this.router.navigate(['/correspondence', { outlets: { popup: null } }]);
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
