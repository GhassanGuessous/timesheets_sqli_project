import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';

import { IImputationType } from 'app/shared/model/imputation-type.model';

@Component({
    selector: 'jhi-imputation-type-detail',
    templateUrl: './imputation-type-detail.component.html'
})
export class ImputationTypeDetailComponent implements OnInit {
    imputationType: IImputationType;

    constructor(protected activatedRoute: ActivatedRoute) {}

    ngOnInit() {
        this.activatedRoute.data.subscribe(({ imputationType }) => {
            this.imputationType = imputationType;
        });
    }

    previousState() {
        window.history.back();
    }
}
