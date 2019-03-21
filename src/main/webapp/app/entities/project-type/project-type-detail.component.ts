import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';

import { IProjectType } from 'app/shared/model/project-type.model';

@Component({
    selector: 'jhi-project-type-detail',
    templateUrl: './project-type-detail.component.html'
})
export class ProjectTypeDetailComponent implements OnInit {
    projectType: IProjectType;

    constructor(protected activatedRoute: ActivatedRoute) {}

    ngOnInit() {
        this.activatedRoute.data.subscribe(({ projectType }) => {
            this.projectType = projectType;
        });
    }

    previousState() {
        window.history.back();
    }
}
