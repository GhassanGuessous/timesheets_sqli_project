import { Injectable } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { Resolve, ActivatedRouteSnapshot, RouterStateSnapshot, Routes } from '@angular/router';
import { JhiPaginationUtil, JhiResolvePagingParams } from 'ng-jhipster';
import { UserRouteAccessService } from 'app/core';
import { Observable, of } from 'rxjs';
import { filter, map } from 'rxjs/operators';
import { ImputationType } from 'app/shared/model/imputation-type.model';
import { ImputationTypeService } from './imputation-type.service';
import { ImputationTypeComponent } from './imputation-type.component';
import { ImputationTypeDetailComponent } from './imputation-type-detail.component';
import { ImputationTypeUpdateComponent } from './imputation-type-update.component';
import { ImputationTypeDeletePopupComponent } from './imputation-type-delete-dialog.component';
import { IImputationType } from 'app/shared/model/imputation-type.model';

@Injectable({ providedIn: 'root' })
export class ImputationTypeResolve implements Resolve<IImputationType> {
    constructor(private service: ImputationTypeService) {}

    resolve(route: ActivatedRouteSnapshot, state: RouterStateSnapshot): Observable<IImputationType> {
        const id = route.params['id'] ? route.params['id'] : null;
        if (id) {
            return this.service.find(id).pipe(
                filter((response: HttpResponse<ImputationType>) => response.ok),
                map((imputationType: HttpResponse<ImputationType>) => imputationType.body)
            );
        }
        return of(new ImputationType());
    }
}

export const imputationTypeRoute: Routes = [
    {
        path: '',
        component: ImputationTypeComponent,
        resolve: {
            pagingParams: JhiResolvePagingParams
        },
        data: {
            authorities: ['ROLE_USER'],
            defaultSort: 'id,asc',
            pageTitle: 'imputationSqliApp.imputationType.home.title'
        },
        canActivate: [UserRouteAccessService]
    },
    {
        path: ':id/view',
        component: ImputationTypeDetailComponent,
        resolve: {
            imputationType: ImputationTypeResolve
        },
        data: {
            authorities: ['ROLE_USER'],
            pageTitle: 'imputationSqliApp.imputationType.home.title'
        },
        canActivate: [UserRouteAccessService]
    },
    {
        path: 'new',
        component: ImputationTypeUpdateComponent,
        resolve: {
            imputationType: ImputationTypeResolve
        },
        data: {
            authorities: ['ROLE_USER'],
            pageTitle: 'imputationSqliApp.imputationType.home.title'
        },
        canActivate: [UserRouteAccessService]
    },
    {
        path: ':id/edit',
        component: ImputationTypeUpdateComponent,
        resolve: {
            imputationType: ImputationTypeResolve
        },
        data: {
            authorities: ['ROLE_USER'],
            pageTitle: 'imputationSqliApp.imputationType.home.title'
        },
        canActivate: [UserRouteAccessService]
    }
];

export const imputationTypePopupRoute: Routes = [
    {
        path: ':id/delete',
        component: ImputationTypeDeletePopupComponent,
        resolve: {
            imputationType: ImputationTypeResolve
        },
        data: {
            authorities: ['ROLE_USER'],
            pageTitle: 'imputationSqliApp.imputationType.home.title'
        },
        canActivate: [UserRouteAccessService],
        outlet: 'popup'
    }
];
