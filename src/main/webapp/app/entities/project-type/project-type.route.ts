import { Injectable } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { Resolve, ActivatedRouteSnapshot, RouterStateSnapshot, Routes } from '@angular/router';
import { JhiPaginationUtil, JhiResolvePagingParams } from 'ng-jhipster';
import { UserRouteAccessService } from 'app/core';
import { Observable, of } from 'rxjs';
import { filter, map } from 'rxjs/operators';
import { ProjectType } from 'app/shared/model/project-type.model';
import { ProjectTypeService } from './project-type.service';
import { ProjectTypeComponent } from './project-type.component';
import { ProjectTypeDetailComponent } from './project-type-detail.component';
import { ProjectTypeUpdateComponent } from './project-type-update.component';
import { ProjectTypeDeletePopupComponent } from './project-type-delete-dialog.component';
import { IProjectType } from 'app/shared/model/project-type.model';

@Injectable({ providedIn: 'root' })
export class ProjectTypeResolve implements Resolve<IProjectType> {
    constructor(private service: ProjectTypeService) {}

    resolve(route: ActivatedRouteSnapshot, state: RouterStateSnapshot): Observable<IProjectType> {
        const id = route.params['id'] ? route.params['id'] : null;
        if (id) {
            return this.service.find(id).pipe(
                filter((response: HttpResponse<ProjectType>) => response.ok),
                map((projectType: HttpResponse<ProjectType>) => projectType.body)
            );
        }
        return of(new ProjectType());
    }
}

export const projectTypeRoute: Routes = [
    {
        path: '',
        component: ProjectTypeComponent,
        resolve: {
            pagingParams: JhiResolvePagingParams
        },
        data: {
            authorities: ['ROLE_ADMIN'],
            defaultSort: 'id,asc',
            pageTitle: 'imputationSqliApp.projectType.home.title'
        },
        canActivate: [UserRouteAccessService]
    },
    {
        path: ':id/view',
        component: ProjectTypeDetailComponent,
        resolve: {
            projectType: ProjectTypeResolve
        },
        data: {
            authorities: ['ROLE_ADMIN'],
            pageTitle: 'imputationSqliApp.projectType.home.title'
        },
        canActivate: [UserRouteAccessService]
    },
    {
        path: 'new',
        component: ProjectTypeUpdateComponent,
        resolve: {
            projectType: ProjectTypeResolve
        },
        data: {
            authorities: ['ROLE_ADMIN'],
            pageTitle: 'imputationSqliApp.projectType.home.title'
        },
        canActivate: [UserRouteAccessService]
    },
    {
        path: ':id/edit',
        component: ProjectTypeUpdateComponent,
        resolve: {
            projectType: ProjectTypeResolve
        },
        data: {
            authorities: ['ROLE_ADMIN'],
            pageTitle: 'imputationSqliApp.projectType.home.title'
        },
        canActivate: [UserRouteAccessService]
    }
];

export const projectTypePopupRoute: Routes = [
    {
        path: ':id/delete',
        component: ProjectTypeDeletePopupComponent,
        resolve: {
            projectType: ProjectTypeResolve
        },
        data: {
            authorities: ['ROLE_ADMIN'],
            pageTitle: 'imputationSqliApp.projectType.home.title'
        },
        canActivate: [UserRouteAccessService],
        outlet: 'popup'
    }
];
