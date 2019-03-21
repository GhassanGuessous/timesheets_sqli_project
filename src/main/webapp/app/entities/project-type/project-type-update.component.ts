import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { HttpResponse, HttpErrorResponse } from '@angular/common/http';
import { Observable } from 'rxjs';
import { filter, map } from 'rxjs/operators';
import { IProjectType } from 'app/shared/model/project-type.model';
import { ProjectTypeService } from './project-type.service';

@Component({
    selector: 'jhi-project-type-update',
    templateUrl: './project-type-update.component.html'
})
export class ProjectTypeUpdateComponent implements OnInit {
    projectType: IProjectType;
    isSaving: boolean;

    constructor(protected projectTypeService: ProjectTypeService, protected activatedRoute: ActivatedRoute) {}

    ngOnInit() {
        this.isSaving = false;
        this.activatedRoute.data.subscribe(({ projectType }) => {
            this.projectType = projectType;
        });
    }

    previousState() {
        window.history.back();
    }

    save() {
        this.isSaving = true;
        if (this.projectType.id !== undefined) {
            this.subscribeToSaveResponse(this.projectTypeService.update(this.projectType));
        } else {
            this.subscribeToSaveResponse(this.projectTypeService.create(this.projectType));
        }
    }

    protected subscribeToSaveResponse(result: Observable<HttpResponse<IProjectType>>) {
        result.subscribe((res: HttpResponse<IProjectType>) => this.onSaveSuccess(), (res: HttpErrorResponse) => this.onSaveError());
    }

    protected onSaveSuccess() {
        this.isSaving = false;
        this.previousState();
    }

    protected onSaveError() {
        this.isSaving = false;
    }
}
