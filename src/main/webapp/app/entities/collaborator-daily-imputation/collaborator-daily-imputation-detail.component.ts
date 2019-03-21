import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';

import { ICollaboratorDailyImputation } from 'app/shared/model/collaborator-daily-imputation.model';

@Component({
    selector: 'jhi-collaborator-daily-imputation-detail',
    templateUrl: './collaborator-daily-imputation-detail.component.html'
})
export class CollaboratorDailyImputationDetailComponent implements OnInit {
    collaboratorDailyImputation: ICollaboratorDailyImputation;

    constructor(protected activatedRoute: ActivatedRoute) {}

    ngOnInit() {
        this.activatedRoute.data.subscribe(({ collaboratorDailyImputation }) => {
            this.collaboratorDailyImputation = collaboratorDailyImputation;
        });
    }

    previousState() {
        window.history.back();
    }
}
