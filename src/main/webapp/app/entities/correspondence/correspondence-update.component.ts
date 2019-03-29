import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { HttpResponse, HttpErrorResponse } from '@angular/common/http';
import { Observable } from 'rxjs';
import { filter, map } from 'rxjs/operators';
import { JhiAlertService } from 'ng-jhipster';
import { ICorrespondence } from 'app/shared/model/correspondence.model';
import { CorrespondenceService } from './correspondence.service';
import { ICollaborator } from 'app/shared/model/collaborator.model';
import { CollaboratorService } from 'app/entities/collaborator';

@Component({
    selector: 'jhi-correspondence-update',
    templateUrl: './correspondence-update.component.html'
})
export class CorrespondenceUpdateComponent implements OnInit {
    correspondence: ICorrespondence;
    isSaving: boolean;

    collaborators: ICollaborator[];

    constructor(
        protected jhiAlertService: JhiAlertService,
        protected correspondenceService: CorrespondenceService,
        protected collaboratorService: CollaboratorService,
        protected activatedRoute: ActivatedRoute
    ) {}

    ngOnInit() {
        this.isSaving = false;
        this.activatedRoute.data.subscribe(({ correspondence }) => {
            this.correspondence = correspondence;
        });
        this.collaboratorService
            .findAllCollaboratorsWithoutPagination({ filter: 'correspondence-is-null' })
            .pipe(
                filter((mayBeOk: HttpResponse<ICollaborator[]>) => mayBeOk.ok),
                map((response: HttpResponse<ICollaborator[]>) => response.body)
            )
            .subscribe(
                (res: ICollaborator[]) => {
                    if (!this.correspondence.collaborator || !this.correspondence.collaborator.id) {
                        this.collaborators = res;
                    } else {
                        this.collaboratorService
                            .find(this.correspondence.collaborator.id)
                            .pipe(
                                filter((subResMayBeOk: HttpResponse<ICollaborator>) => subResMayBeOk.ok),
                                map((subResponse: HttpResponse<ICollaborator>) => subResponse.body)
                            )
                            .subscribe(
                                (subRes: ICollaborator) => (this.collaborators = [subRes].concat(res)),
                                (subRes: HttpErrorResponse) => this.onError(subRes.message)
                            );
                    }
                },
                (res: HttpErrorResponse) => this.onError(res.message)
            );
    }

    previousState() {
        window.history.back();
    }

    save() {
        this.isSaving = true;
        if (this.correspondence.id !== undefined) {
            this.subscribeToSaveResponse(this.correspondenceService.update(this.correspondence));
        } else {
            this.subscribeToSaveResponse(this.correspondenceService.create(this.correspondence));
        }
    }

    protected subscribeToSaveResponse(result: Observable<HttpResponse<ICorrespondence>>) {
        result.subscribe((res: HttpResponse<ICorrespondence>) => this.onSaveSuccess(), (res: HttpErrorResponse) => this.onSaveError());
    }

    protected onSaveSuccess() {
        this.isSaving = false;
        this.previousState();
    }

    protected onSaveError() {
        this.isSaving = false;
    }

    protected onError(errorMessage: string) {
        this.jhiAlertService.error(errorMessage, null, null);
    }

    trackCollaboratorById(index: number, item: ICollaborator) {
        return item.id;
    }
}
