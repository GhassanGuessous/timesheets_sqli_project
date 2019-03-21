import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { HttpResponse, HttpErrorResponse } from '@angular/common/http';
import { Observable } from 'rxjs';
import { filter, map } from 'rxjs/operators';
import { JhiAlertService } from 'ng-jhipster';
import { ICollaboratorDailyImputation } from 'app/shared/model/collaborator-daily-imputation.model';
import { CollaboratorDailyImputationService } from './collaborator-daily-imputation.service';
import { ICollaboratorMonthlyImputation } from 'app/shared/model/collaborator-monthly-imputation.model';
import { CollaboratorMonthlyImputationService } from 'app/entities/collaborator-monthly-imputation';

@Component({
    selector: 'jhi-collaborator-daily-imputation-update',
    templateUrl: './collaborator-daily-imputation-update.component.html'
})
export class CollaboratorDailyImputationUpdateComponent implements OnInit {
    collaboratorDailyImputation: ICollaboratorDailyImputation;
    isSaving: boolean;

    collaboratormonthlyimputations: ICollaboratorMonthlyImputation[];

    constructor(
        protected jhiAlertService: JhiAlertService,
        protected collaboratorDailyImputationService: CollaboratorDailyImputationService,
        protected collaboratorMonthlyImputationService: CollaboratorMonthlyImputationService,
        protected activatedRoute: ActivatedRoute
    ) {}

    ngOnInit() {
        this.isSaving = false;
        this.activatedRoute.data.subscribe(({ collaboratorDailyImputation }) => {
            this.collaboratorDailyImputation = collaboratorDailyImputation;
        });
        this.collaboratorMonthlyImputationService
            .query()
            .pipe(
                filter((mayBeOk: HttpResponse<ICollaboratorMonthlyImputation[]>) => mayBeOk.ok),
                map((response: HttpResponse<ICollaboratorMonthlyImputation[]>) => response.body)
            )
            .subscribe(
                (res: ICollaboratorMonthlyImputation[]) => (this.collaboratormonthlyimputations = res),
                (res: HttpErrorResponse) => this.onError(res.message)
            );
    }

    previousState() {
        window.history.back();
    }

    save() {
        this.isSaving = true;
        if (this.collaboratorDailyImputation.id !== undefined) {
            this.subscribeToSaveResponse(this.collaboratorDailyImputationService.update(this.collaboratorDailyImputation));
        } else {
            this.subscribeToSaveResponse(this.collaboratorDailyImputationService.create(this.collaboratorDailyImputation));
        }
    }

    protected subscribeToSaveResponse(result: Observable<HttpResponse<ICollaboratorDailyImputation>>) {
        result.subscribe(
            (res: HttpResponse<ICollaboratorDailyImputation>) => this.onSaveSuccess(),
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

    trackCollaboratorMonthlyImputationById(index: number, item: ICollaboratorMonthlyImputation) {
        return item.id;
    }
}
