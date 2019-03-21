import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';

import { IImputation } from 'app/shared/model/imputation.model';

@Component({
    selector: 'jhi-imputation-detail',
    templateUrl: './imputation-detail.component.html'
})
export class ImputationDetailComponent implements OnInit {
    imputation: IImputation;

    constructor(protected activatedRoute: ActivatedRoute) {}

    ngOnInit() {
        this.activatedRoute.data.subscribe(({ imputation }) => {
            this.imputation = imputation;
        });
    }

    previousState() {
        window.history.back();
    }
}
