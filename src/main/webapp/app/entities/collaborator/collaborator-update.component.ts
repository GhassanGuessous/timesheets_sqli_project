import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { HttpResponse, HttpErrorResponse } from '@angular/common/http';
import { Observable } from 'rxjs';
import { filter, map } from 'rxjs/operators';
import { JhiAlertService } from 'ng-jhipster';
import { ICollaborator } from 'app/shared/model/collaborator.model';
import { CollaboratorService } from './collaborator.service';
import { ITeam } from 'app/shared/model/team.model';
import { TeamService } from 'app/entities/team';
import { IActivity } from 'app/shared/model/activity.model';
import { ActivityService } from 'app/entities/activity';

@Component({
    selector: 'jhi-collaborator-update',
    templateUrl: './collaborator-update.component.html'
})
export class CollaboratorUpdateComponent implements OnInit {
    collaborator: ICollaborator;
    isSaving: boolean;

    teams: ITeam[];

    activities: IActivity[];

    constructor(
        protected jhiAlertService: JhiAlertService,
        protected collaboratorService: CollaboratorService,
        protected teamService: TeamService,
        protected activityService: ActivityService,
        protected activatedRoute: ActivatedRoute
    ) {}

    ngOnInit() {
        this.isSaving = false;
        this.activatedRoute.data.subscribe(({ collaborator }) => {
            this.collaborator = collaborator;
        });
        this.teamService
            .findAllTeamsWithoutPagination()
            .pipe(
                filter((mayBeOk: HttpResponse<ITeam[]>) => mayBeOk.ok),
                map((response: HttpResponse<ITeam[]>) => response.body)
            )
            .subscribe((res: ITeam[]) => (this.teams = res), (res: HttpErrorResponse) => this.onError(res.message));
        this.activityService
            .query()
            .pipe(
                filter((mayBeOk: HttpResponse<IActivity[]>) => mayBeOk.ok),
                map((response: HttpResponse<IActivity[]>) => response.body)
            )
            .subscribe((res: IActivity[]) => (this.activities = res), (res: HttpErrorResponse) => this.onError(res.message));
    }

    previousState() {
        window.history.back();
    }

    save() {
        this.isSaving = true;
        if (this.collaborator.id !== undefined) {
            this.subscribeToSaveResponse(this.collaboratorService.update(this.collaborator));
        } else {
            this.subscribeToSaveResponse(this.collaboratorService.create(this.collaborator));
        }
    }

    protected subscribeToSaveResponse(result: Observable<HttpResponse<ICollaborator>>) {
        result.subscribe((res: HttpResponse<ICollaborator>) => this.onSaveSuccess(), (res: HttpErrorResponse) => this.onSaveError());
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

    trackTeamById(index: number, item: ITeam) {
        return item.id;
    }

    trackActivityById(index: number, item: IActivity) {
        return item.id;
    }
}
