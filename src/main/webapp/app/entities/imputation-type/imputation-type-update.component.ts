import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { HttpResponse, HttpErrorResponse } from '@angular/common/http';
import { Observable } from 'rxjs';
import { filter, map } from 'rxjs/operators';
import { IImputationType } from 'app/shared/model/imputation-type.model';
import { ImputationTypeService } from './imputation-type.service';

@Component({
    selector: 'jhi-imputation-type-update',
    templateUrl: './imputation-type-update.component.html'
})
export class ImputationTypeUpdateComponent implements OnInit {
    imputationType: IImputationType;
    isSaving: boolean;

    constructor(protected imputationTypeService: ImputationTypeService, protected activatedRoute: ActivatedRoute) {}

    ngOnInit() {
        this.isSaving = false;
        this.activatedRoute.data.subscribe(({ imputationType }) => {
            this.imputationType = imputationType;
        });
    }

    previousState() {
        window.history.back();
    }

    save() {
        this.isSaving = true;
        if (this.imputationType.id !== undefined) {
            this.subscribeToSaveResponse(this.imputationTypeService.update(this.imputationType));
        } else {
            this.subscribeToSaveResponse(this.imputationTypeService.create(this.imputationType));
        }
    }

    protected subscribeToSaveResponse(result: Observable<HttpResponse<IImputationType>>) {
        result.subscribe((res: HttpResponse<IImputationType>) => this.onSaveSuccess(), (res: HttpErrorResponse) => this.onSaveError());
    }

    protected onSaveSuccess() {
        this.isSaving = false;
        this.previousState();
    }

    protected onSaveError() {
        this.isSaving = false;
    }
}
