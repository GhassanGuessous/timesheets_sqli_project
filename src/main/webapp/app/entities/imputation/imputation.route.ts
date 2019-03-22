import { Injectable } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { Resolve, ActivatedRouteSnapshot, RouterStateSnapshot, Routes } from '@angular/router';
import { JhiPaginationUtil, JhiResolvePagingParams } from 'ng-jhipster';
import { UserRouteAccessService } from 'app/core';
import { Observable, of } from 'rxjs';
import { filter, map } from 'rxjs/operators';
import { Imputation } from 'app/shared/model/imputation.model';
import { ImputationService } from './imputation.service';
import { ImputationComponent } from './imputation.component';
import { ImputationDetailComponent } from './imputation-detail.component';
import { ImputationUpdateComponent } from './imputation-update.component';
import { ImputationDeletePopupComponent } from './imputation-delete-dialog.component';
import { IImputation } from 'app/shared/model/imputation.model';

@Injectable({ providedIn: 'root' })
export class ImputationResolve implements Resolve<IImputation> {
    constructor(private service: ImputationService) {}

    resolve(route: ActivatedRouteSnapshot, state: RouterStateSnapshot): Observable<IImputation> {
        const id = route.params['id'] ? route.params['id'] : null;
        if (id) {
            return this.service.find(id).pipe(
                filter((response: HttpResponse<Imputation>) => response.ok),
                map((imputation: HttpResponse<Imputation>) => imputation.body)
            );
        }
        return of(new Imputation());
    }
}

export const imputationRoute: Routes = [
    {
        path: '',
        component: ImputationComponent,
        resolve: {
            pagingParams: JhiResolvePagingParams
        },
        data: {
            authorities: ['ROLE_ADMIN'],
            defaultSort: 'id,asc',
            pageTitle: 'imputationSqliApp.imputation.home.title'
        },
        canActivate: [UserRouteAccessService]
    },
    {
        path: ':id/view',
        component: ImputationDetailComponent,
        resolve: {
            imputation: ImputationResolve
        },
        data: {
            authorities: ['ROLE_ADMIN'],
            pageTitle: 'imputationSqliApp.imputation.home.title'
        },
        canActivate: [UserRouteAccessService]
    },
    {
        path: 'new',
        component: ImputationUpdateComponent,
        resolve: {
            imputation: ImputationResolve
        },
        data: {
            authorities: ['ROLE_ADMIN'],
            pageTitle: 'imputationSqliApp.imputation.home.title'
        },
        canActivate: [UserRouteAccessService]
    },
    {
        path: ':id/edit',
        component: ImputationUpdateComponent,
        resolve: {
            imputation: ImputationResolve
        },
        data: {
            authorities: ['ROLE_ADMIN'],
            pageTitle: 'imputationSqliApp.imputation.home.title'
        },
        canActivate: [UserRouteAccessService]
    }
];

export const imputationPopupRoute: Routes = [
    {
        path: ':id/delete',
        component: ImputationDeletePopupComponent,
        resolve: {
            imputation: ImputationResolve
        },
        data: {
            authorities: ['ROLE_ADMIN'],
            pageTitle: 'imputationSqliApp.imputation.home.title'
        },
        canActivate: [UserRouteAccessService],
        outlet: 'popup'
    }
];
