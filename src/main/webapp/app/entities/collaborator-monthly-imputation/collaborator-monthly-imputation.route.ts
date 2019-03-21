import { Injectable } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { Resolve, ActivatedRouteSnapshot, RouterStateSnapshot, Routes } from '@angular/router';
import { JhiPaginationUtil, JhiResolvePagingParams } from 'ng-jhipster';
import { UserRouteAccessService } from 'app/core';
import { Observable, of } from 'rxjs';
import { filter, map } from 'rxjs/operators';
import { CollaboratorMonthlyImputation } from 'app/shared/model/collaborator-monthly-imputation.model';
import { CollaboratorMonthlyImputationService } from './collaborator-monthly-imputation.service';
import { CollaboratorMonthlyImputationComponent } from './collaborator-monthly-imputation.component';
import { CollaboratorMonthlyImputationDetailComponent } from './collaborator-monthly-imputation-detail.component';
import { CollaboratorMonthlyImputationUpdateComponent } from './collaborator-monthly-imputation-update.component';
import { CollaboratorMonthlyImputationDeletePopupComponent } from './collaborator-monthly-imputation-delete-dialog.component';
import { ICollaboratorMonthlyImputation } from 'app/shared/model/collaborator-monthly-imputation.model';

@Injectable({ providedIn: 'root' })
export class CollaboratorMonthlyImputationResolve implements Resolve<ICollaboratorMonthlyImputation> {
    constructor(private service: CollaboratorMonthlyImputationService) {}

    resolve(route: ActivatedRouteSnapshot, state: RouterStateSnapshot): Observable<ICollaboratorMonthlyImputation> {
        const id = route.params['id'] ? route.params['id'] : null;
        if (id) {
            return this.service.find(id).pipe(
                filter((response: HttpResponse<CollaboratorMonthlyImputation>) => response.ok),
                map((collaboratorMonthlyImputation: HttpResponse<CollaboratorMonthlyImputation>) => collaboratorMonthlyImputation.body)
            );
        }
        return of(new CollaboratorMonthlyImputation());
    }
}

export const collaboratorMonthlyImputationRoute: Routes = [
    {
        path: '',
        component: CollaboratorMonthlyImputationComponent,
        resolve: {
            pagingParams: JhiResolvePagingParams
        },
        data: {
            authorities: ['ROLE_USER'],
            defaultSort: 'id,asc',
            pageTitle: 'imputationSqliApp.collaboratorMonthlyImputation.home.title'
        },
        canActivate: [UserRouteAccessService]
    },
    {
        path: ':id/view',
        component: CollaboratorMonthlyImputationDetailComponent,
        resolve: {
            collaboratorMonthlyImputation: CollaboratorMonthlyImputationResolve
        },
        data: {
            authorities: ['ROLE_USER'],
            pageTitle: 'imputationSqliApp.collaboratorMonthlyImputation.home.title'
        },
        canActivate: [UserRouteAccessService]
    },
    {
        path: 'new',
        component: CollaboratorMonthlyImputationUpdateComponent,
        resolve: {
            collaboratorMonthlyImputation: CollaboratorMonthlyImputationResolve
        },
        data: {
            authorities: ['ROLE_USER'],
            pageTitle: 'imputationSqliApp.collaboratorMonthlyImputation.home.title'
        },
        canActivate: [UserRouteAccessService]
    },
    {
        path: ':id/edit',
        component: CollaboratorMonthlyImputationUpdateComponent,
        resolve: {
            collaboratorMonthlyImputation: CollaboratorMonthlyImputationResolve
        },
        data: {
            authorities: ['ROLE_USER'],
            pageTitle: 'imputationSqliApp.collaboratorMonthlyImputation.home.title'
        },
        canActivate: [UserRouteAccessService]
    }
];

export const collaboratorMonthlyImputationPopupRoute: Routes = [
    {
        path: ':id/delete',
        component: CollaboratorMonthlyImputationDeletePopupComponent,
        resolve: {
            collaboratorMonthlyImputation: CollaboratorMonthlyImputationResolve
        },
        data: {
            authorities: ['ROLE_USER'],
            pageTitle: 'imputationSqliApp.collaboratorMonthlyImputation.home.title'
        },
        canActivate: [UserRouteAccessService],
        outlet: 'popup'
    }
];
