import { Component, OnInit, OnDestroy } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';

import { NgbActiveModal, NgbModal, NgbModalRef } from '@ng-bootstrap/ng-bootstrap';
import { JhiEventManager } from 'ng-jhipster';

import { IProjectType } from 'app/shared/model/project-type.model';
import { ProjectTypeService } from './project-type.service';

@Component({
    selector: 'jhi-project-type-delete-dialog',
    templateUrl: './project-type-delete-dialog.component.html'
})
export class ProjectTypeDeleteDialogComponent {
    projectType: IProjectType;

    constructor(
        protected projectTypeService: ProjectTypeService,
        public activeModal: NgbActiveModal,
        protected eventManager: JhiEventManager
    ) {}

    clear() {
        this.activeModal.dismiss('cancel');
    }

    confirmDelete(id: number) {
        this.projectTypeService.delete(id).subscribe(response => {
            this.eventManager.broadcast({
                name: 'projectTypeListModification',
                content: 'Deleted an projectType'
            });
            this.activeModal.dismiss(true);
        });
    }
}

@Component({
    selector: 'jhi-project-type-delete-popup',
    template: ''
})
export class ProjectTypeDeletePopupComponent implements OnInit, OnDestroy {
    protected ngbModalRef: NgbModalRef;

    constructor(protected activatedRoute: ActivatedRoute, protected router: Router, protected modalService: NgbModal) {}

    ngOnInit() {
        this.activatedRoute.data.subscribe(({ projectType }) => {
            setTimeout(() => {
                this.ngbModalRef = this.modalService.open(ProjectTypeDeleteDialogComponent as Component, {
                    size: 'lg',
                    backdrop: 'static'
                });
                this.ngbModalRef.componentInstance.projectType = projectType;
                this.ngbModalRef.result.then(
                    result => {
                        this.router.navigate(['/project-type', { outlets: { popup: null } }]);
                        this.ngbModalRef = null;
                    },
                    reason => {
                        this.router.navigate(['/project-type', { outlets: { popup: null } }]);
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
