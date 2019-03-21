import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { HttpResponse, HttpErrorResponse } from '@angular/common/http';
import { Observable } from 'rxjs';
import { filter, map } from 'rxjs/operators';
import { JhiAlertService } from 'ng-jhipster';
import { IImputation } from 'app/shared/model/imputation.model';
import { ImputationService } from './imputation.service';
import { IImputationType } from 'app/shared/model/imputation-type.model';
import { ImputationTypeService } from 'app/entities/imputation-type';

@Component({
    selector: 'jhi-imputation-update',
    templateUrl: './imputation-update.component.html'
})
export class ImputationUpdateComponent implements OnInit {
    imputation: IImputation;
    isSaving: boolean;

    imputationtypes: IImputationType[];

    constructor(
        protected jhiAlertService: JhiAlertService,
        protected imputationService: ImputationService,
        protected imputationTypeService: ImputationTypeService,
        protected activatedRoute: ActivatedRoute
    ) {}

    ngOnInit() {
        this.isSaving = false;
        this.activatedRoute.data.subscribe(({ imputation }) => {
            this.imputation = imputation;
        });
        this.imputationTypeService
            .query()
            .pipe(
                filter((mayBeOk: HttpResponse<IImputationType[]>) => mayBeOk.ok),
                map((response: HttpResponse<IImputationType[]>) => response.body)
            )
            .subscribe((res: IImputationType[]) => (this.imputationtypes = res), (res: HttpErrorResponse) => this.onError(res.message));
    }

    previousState() {
        window.history.back();
    }

    save() {
        this.isSaving = true;
        if (this.imputation.id !== undefined) {
            this.subscribeToSaveResponse(this.imputationService.update(this.imputation));
        } else {
            this.subscribeToSaveResponse(this.imputationService.create(this.imputation));
        }
    }

    protected subscribeToSaveResponse(result: Observable<HttpResponse<IImputation>>) {
        result.subscribe((res: HttpResponse<IImputation>) => this.onSaveSuccess(), (res: HttpErrorResponse) => this.onSaveError());
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

    trackImputationTypeById(index: number, item: IImputationType) {
        return item.id;
    }
}
