import { Injectable } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { Resolve, ActivatedRouteSnapshot, RouterStateSnapshot, Routes } from '@angular/router';
import { JhiPaginationUtil, JhiResolvePagingParams } from 'ng-jhipster';
import { UserRouteAccessService } from 'app/core';
import { Observable, of } from 'rxjs';
import { filter, map } from 'rxjs/operators';
import { Correspondence } from 'app/shared/model/correspondence.model';
import { CorrespondenceService } from './correspondence.service';
import { CorrespondenceComponent } from './correspondence.component';
import { CorrespondenceDetailComponent } from './correspondence-detail.component';
import { CorrespondenceUpdateComponent } from './correspondence-update.component';
import { CorrespondenceDeletePopupComponent } from './correspondence-delete-dialog.component';
import { ICorrespondence } from 'app/shared/model/correspondence.model';

@Injectable({ providedIn: 'root' })
export class CorrespondenceResolve implements Resolve<ICorrespondence> {
    constructor(private service: CorrespondenceService) {}

    resolve(route: ActivatedRouteSnapshot, state: RouterStateSnapshot): Observable<ICorrespondence> {
        const id = route.params['id'] ? route.params['id'] : null;
        if (id) {
            return this.service.find(id).pipe(
                filter((response: HttpResponse<Correspondence>) => response.ok),
                map((correspondence: HttpResponse<Correspondence>) => correspondence.body)
            );
        }
        return of(new Correspondence());
    }
}

export const correspondenceRoute: Routes = [
    {
        path: '',
        component: CorrespondenceComponent,
        resolve: {
            pagingParams: JhiResolvePagingParams
        },
        data: {
            authorities: ['ROLE_ADMIN'],
            defaultSort: 'id,asc',
            pageTitle: 'imputationSqliApp.correspondence.home.title'
        },
        canActivate: [UserRouteAccessService]
    },
    {
        path: ':id/view',
        component: CorrespondenceDetailComponent,
        resolve: {
            correspondence: CorrespondenceResolve
        },
        data: {
            authorities: ['ROLE_ADMIN'],
            pageTitle: 'imputationSqliApp.correspondence.home.title'
        },
        canActivate: [UserRouteAccessService]
    },
    {
        path: 'new',
        component: CorrespondenceUpdateComponent,
        resolve: {
            correspondence: CorrespondenceResolve
        },
        data: {
            authorities: ['ROLE_ADMIN'],
            pageTitle: 'imputationSqliApp.correspondence.home.title'
        },
        canActivate: [UserRouteAccessService]
    },
    {
        path: ':id/edit',
        component: CorrespondenceUpdateComponent,
        resolve: {
            correspondence: CorrespondenceResolve
        },
        data: {
            authorities: ['ROLE_ADMIN'],
            pageTitle: 'imputationSqliApp.correspondence.home.title'
        },
        canActivate: [UserRouteAccessService]
    }
];

export const correspondencePopupRoute: Routes = [
    {
        path: ':id/delete',
        component: CorrespondenceDeletePopupComponent,
        resolve: {
            correspondence: CorrespondenceResolve
        },
        data: {
            authorities: ['ROLE_ADMIN'],
            pageTitle: 'imputationSqliApp.correspondence.home.title'
        },
        canActivate: [UserRouteAccessService],
        outlet: 'popup'
    }
];
