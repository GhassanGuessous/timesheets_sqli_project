import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { HttpResponse, HttpErrorResponse } from '@angular/common/http';
import { Observable } from 'rxjs';
import { filter, map } from 'rxjs/operators';
import { IActivity } from 'app/shared/model/activity.model';
import { ActivityService } from './activity.service';

@Component({
    selector: 'jhi-activity-update',
    templateUrl: './activity-update.component.html'
})
export class ActivityUpdateComponent implements OnInit {
    activity: IActivity;
    isSaving: boolean;

    constructor(protected activityService: ActivityService, protected activatedRoute: ActivatedRoute) {}

    ngOnInit() {
        this.isSaving = false;
        this.activatedRoute.data.subscribe(({ activity }) => {
            this.activity = activity;
        });
    }

    previousState() {
        window.history.back();
    }

    save() {
        this.isSaving = true;
        if (this.activity.id !== undefined) {
            this.subscribeToSaveResponse(this.activityService.update(this.activity));
        } else {
            this.subscribeToSaveResponse(this.activityService.create(this.activity));
        }
    }

    protected subscribeToSaveResponse(result: Observable<HttpResponse<IActivity>>) {
        result.subscribe((res: HttpResponse<IActivity>) => this.onSaveSuccess(), (res: HttpErrorResponse) => this.onSaveError());
    }

    protected onSaveSuccess() {
        this.isSaving = false;
        this.previousState();
    }

    protected onSaveError() {
        this.isSaving = false;
    }
}
