import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { HttpResponse, HttpErrorResponse } from '@angular/common/http';
import { Observable } from 'rxjs';
import { filter, map } from 'rxjs/operators';
import { JhiAlertService } from 'ng-jhipster';
import { ICollaboratorMonthlyImputation } from 'app/shared/model/collaborator-monthly-imputation.model';
import { CollaboratorMonthlyImputationService } from './collaborator-monthly-imputation.service';
import { ICollaborator } from 'app/shared/model/collaborator.model';
import { CollaboratorService } from 'app/entities/collaborator';
import { IImputation } from 'app/shared/model/imputation.model';
import { ImputationService } from 'app/entities/imputation';

@Component({
    selector: 'jhi-collaborator-monthly-imputation-update',
    templateUrl: './collaborator-monthly-imputation-update.component.html'
})
export class CollaboratorMonthlyImputationUpdateComponent implements OnInit {
    collaboratorMonthlyImputation: ICollaboratorMonthlyImputation;
    isSaving: boolean;

    collaborators: ICollaborator[];

    imputations: IImputation[];

    constructor(
        protected jhiAlertService: JhiAlertService,
        protected collaboratorMonthlyImputationService: CollaboratorMonthlyImputationService,
        protected collaboratorService: CollaboratorService,
        protected imputationService: ImputationService,
        protected activatedRoute: ActivatedRoute
    ) {}

    ngOnInit() {
        this.isSaving = false;
        this.activatedRoute.data.subscribe(({ collaboratorMonthlyImputation }) => {
            this.collaboratorMonthlyImputation = collaboratorMonthlyImputation;
        });
        this.collaboratorService
            .query()
            .pipe(
                filter((mayBeOk: HttpResponse<ICollaborator[]>) => mayBeOk.ok),
                map((response: HttpResponse<ICollaborator[]>) => response.body)
            )
            .subscribe((res: ICollaborator[]) => (this.collaborators = res), (res: HttpErrorResponse) => this.onError(res.message));
        this.imputationService
            .query()
            .pipe(
                filter((mayBeOk: HttpResponse<IImputation[]>) => mayBeOk.ok),
                map((response: HttpResponse<IImputation[]>) => response.body)
            )
            .subscribe((res: IImputation[]) => (this.imputations = res), (res: HttpErrorResponse) => this.onError(res.message));
    }

    previousState() {
        window.history.back();
    }

    save() {
        this.isSaving = true;
        if (this.collaboratorMonthlyImputation.id !== undefined) {
            this.subscribeToSaveResponse(this.collaboratorMonthlyImputationService.update(this.collaboratorMonthlyImputation));
        } else {
            this.subscribeToSaveResponse(this.collaboratorMonthlyImputationService.create(this.collaboratorMonthlyImputation));
        }
    }

    protected subscribeToSaveResponse(result: Observable<HttpResponse<ICollaboratorMonthlyImputation>>) {
        result.subscribe(
            (res: HttpResponse<ICollaboratorMonthlyImputation>) => this.onSaveSuccess(),
            (res: HttpErrorResponse) => this.onSaveError()
        );
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

    trackImputationById(index: number, item: IImputation) {
        return item.id;
    }
}
