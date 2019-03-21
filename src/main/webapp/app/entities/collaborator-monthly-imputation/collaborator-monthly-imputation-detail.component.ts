import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';

import { ICollaboratorMonthlyImputation } from 'app/shared/model/collaborator-monthly-imputation.model';

@Component({
    selector: 'jhi-collaborator-monthly-imputation-detail',
    templateUrl: './collaborator-monthly-imputation-detail.component.html'
})
export class CollaboratorMonthlyImputationDetailComponent implements OnInit {
    collaboratorMonthlyImputation: ICollaboratorMonthlyImputation;

    constructor(protected activatedRoute: ActivatedRoute) {}

    ngOnInit() {
        this.activatedRoute.data.subscribe(({ collaboratorMonthlyImputation }) => {
            this.collaboratorMonthlyImputation = collaboratorMonthlyImputation;
        });
    }

    previousState() {
        window.history.back();
    }
}
