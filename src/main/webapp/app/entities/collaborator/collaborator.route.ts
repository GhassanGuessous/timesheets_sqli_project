import { Injectable } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { Resolve, ActivatedRouteSnapshot, RouterStateSnapshot, Routes } from '@angular/router';
import { JhiPaginationUtil, JhiResolvePagingParams } from 'ng-jhipster';
import { UserRouteAccessService } from 'app/core';
import { Observable, of } from 'rxjs';
import { filter, map } from 'rxjs/operators';
import { Collaborator } from 'app/shared/model/collaborator.model';
import { CollaboratorService } from './collaborator.service';
import { CollaboratorComponent } from './collaborator.component';
import { CollaboratorDetailComponent } from './collaborator-detail.component';
import { CollaboratorUpdateComponent } from './collaborator-update.component';
import { CollaboratorDeletePopupComponent } from './collaborator-delete-dialog.component';
import { ICollaborator } from 'app/shared/model/collaborator.model';

@Injectable({ providedIn: 'root' })
export class CollaboratorResolve implements Resolve<ICollaborator> {
    constructor(private service: CollaboratorService) {}

    resolve(route: ActivatedRouteSnapshot, state: RouterStateSnapshot): Observable<ICollaborator> {
        const id = route.params['id'] ? route.params['id'] : null;
        if (id) {
            return this.service.find(id).pipe(
                filter((response: HttpResponse<Collaborator>) => response.ok),
                map((collaborator: HttpResponse<Collaborator>) => collaborator.body)
            );
        }
        return of(new Collaborator());
    }
}

export const collaboratorRoute: Routes = [
    {
        path: '',
        component: CollaboratorComponent,
        resolve: {
            pagingParams: JhiResolvePagingParams
        },
        data: {
            authorities: ['ROLE_USER'],
            defaultSort: 'id,asc',
            pageTitle: 'imputationSqliApp.collaborator.home.title'
        },
        canActivate: [UserRouteAccessService]
    },
    {
        path: ':id/view',
        component: CollaboratorDetailComponent,
        resolve: {
            collaborator: CollaboratorResolve
        },
        data: {
            authorities: ['ROLE_USER'],
            pageTitle: 'imputationSqliApp.collaborator.home.title'
        },
        canActivate: [UserRouteAccessService]
    },
    {
        path: 'new',
        component: CollaboratorUpdateComponent,
        resolve: {
            collaborator: CollaboratorResolve
        },
        data: {
            authorities: ['ROLE_USER'],
            pageTitle: 'imputationSqliApp.collaborator.home.title'
        },
        canActivate: [UserRouteAccessService]
    },
    {
        path: ':id/edit',
        component: CollaboratorUpdateComponent,
        resolve: {
            collaborator: CollaboratorResolve
        },
        data: {
            authorities: ['ROLE_USER'],
            pageTitle: 'imputationSqliApp.collaborator.home.title'
        },
        canActivate: [UserRouteAccessService]
    }
];

export const collaboratorPopupRoute: Routes = [
    {
        path: ':id/delete',
        component: CollaboratorDeletePopupComponent,
        resolve: {
            collaborator: CollaboratorResolve
        },
        data: {
            authorities: ['ROLE_USER'],
            pageTitle: 'imputationSqliApp.collaborator.home.title'
        },
        canActivate: [UserRouteAccessService],
        outlet: 'popup'
    }
];
