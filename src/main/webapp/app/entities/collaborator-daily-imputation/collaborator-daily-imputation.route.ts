import { Injectable } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { Resolve, ActivatedRouteSnapshot, RouterStateSnapshot, Routes } from '@angular/router';
import { JhiPaginationUtil, JhiResolvePagingParams } from 'ng-jhipster';
import { UserRouteAccessService } from 'app/core';
import { Observable, of } from 'rxjs';
import { filter, map } from 'rxjs/operators';
import { CollaboratorDailyImputation } from 'app/shared/model/collaborator-daily-imputation.model';
import { CollaboratorDailyImputationService } from './collaborator-daily-imputation.service';
import { CollaboratorDailyImputationComponent } from './collaborator-daily-imputation.component';
import { CollaboratorDailyImputationDetailComponent } from './collaborator-daily-imputation-detail.component';
import { CollaboratorDailyImputationUpdateComponent } from './collaborator-daily-imputation-update.component';
import { CollaboratorDailyImputationDeletePopupComponent } from './collaborator-daily-imputation-delete-dialog.component';
import { ICollaboratorDailyImputation } from 'app/shared/model/collaborator-daily-imputation.model';

@Injectable({ providedIn: 'root' })
export class CollaboratorDailyImputationResolve implements Resolve<ICollaboratorDailyImputation> {
    constructor(private service: CollaboratorDailyImputationService) {}

    resolve(route: ActivatedRouteSnapshot, state: RouterStateSnapshot): Observable<ICollaboratorDailyImputation> {
        const id = route.params['id'] ? route.params['id'] : null;
        if (id) {
            return this.service.find(id).pipe(
                filter((response: HttpResponse<CollaboratorDailyImputation>) => response.ok),
                map((collaboratorDailyImputation: HttpResponse<CollaboratorDailyImputation>) => collaboratorDailyImputation.body)
            );
        }
        return of(new CollaboratorDailyImputation());
    }
}

export const collaboratorDailyImputationRoute: Routes = [
    {
        path: '',
        component: CollaboratorDailyImputationComponent,
        resolve: {
            pagingParams: JhiResolvePagingParams
        },
        data: {
            authorities: ['ROLE_ADMIN'],
            defaultSort: 'id,asc',
            pageTitle: 'imputationSqliApp.collaboratorDailyImputation.home.title'
        },
        canActivate: [UserRouteAccessService]
    },
    {
        path: ':id/view',
        component: CollaboratorDailyImputationDetailComponent,
        resolve: {
            collaboratorDailyImputation: CollaboratorDailyImputationResolve
        },
        data: {
            authorities: ['ROLE_ADMIN'],
            pageTitle: 'imputationSqliApp.collaboratorDailyImputation.home.title'
        },
        canActivate: [UserRouteAccessService]
    },
    {
        path: 'new',
        component: CollaboratorDailyImputationUpdateComponent,
        resolve: {
            collaboratorDailyImputation: CollaboratorDailyImputationResolve
        },
        data: {
            authorities: ['ROLE_ADMIN'],
            pageTitle: 'imputationSqliApp.collaboratorDailyImputation.home.title'
        },
        canActivate: [UserRouteAccessService]
    },
    {
        path: ':id/edit',
        component: CollaboratorDailyImputationUpdateComponent,
        resolve: {
            collaboratorDailyImputation: CollaboratorDailyImputationResolve
        },
        data: {
            authorities: ['ROLE_ADMIN'],
            pageTitle: 'imputationSqliApp.collaboratorDailyImputation.home.title'
        },
        canActivate: [UserRouteAccessService]
    }
];

export const collaboratorDailyImputationPopupRoute: Routes = [
    {
        path: ':id/delete',
        component: CollaboratorDailyImputationDeletePopupComponent,
        resolve: {
            collaboratorDailyImputation: CollaboratorDailyImputationResolve
        },
        data: {
            authorities: ['ROLE_ADMIN'],
            pageTitle: 'imputationSqliApp.collaboratorDailyImputation.home.title'
        },
        canActivate: [UserRouteAccessService],
        outlet: 'popup'
    }
];
