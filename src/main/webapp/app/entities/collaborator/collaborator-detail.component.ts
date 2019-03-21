import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';

import { ICollaborator } from 'app/shared/model/collaborator.model';

@Component({
    selector: 'jhi-collaborator-detail',
    templateUrl: './collaborator-detail.component.html'
})
export class CollaboratorDetailComponent implements OnInit {
    collaborator: ICollaborator;

    constructor(protected activatedRoute: ActivatedRoute) {}

    ngOnInit() {
        this.activatedRoute.data.subscribe(({ collaborator }) => {
            this.collaborator = collaborator;
        });
    }

    previousState() {
        window.history.back();
    }
}
