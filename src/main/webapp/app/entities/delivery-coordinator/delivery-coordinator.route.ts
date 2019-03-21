import { Injectable } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { Resolve, ActivatedRouteSnapshot, RouterStateSnapshot, Routes } from '@angular/router';
import { UserRouteAccessService } from 'app/core';
import { Observable, of } from 'rxjs';
import { filter, map } from 'rxjs/operators';
import { DeliveryCoordinator } from 'app/shared/model/delivery-coordinator.model';
import { DeliveryCoordinatorService } from './delivery-coordinator.service';
import { DeliveryCoordinatorComponent } from './delivery-coordinator.component';
import { DeliveryCoordinatorDetailComponent } from './delivery-coordinator-detail.component';
import { DeliveryCoordinatorUpdateComponent } from './delivery-coordinator-update.component';
import { DeliveryCoordinatorDeletePopupComponent } from './delivery-coordinator-delete-dialog.component';
import { IDeliveryCoordinator } from 'app/shared/model/delivery-coordinator.model';

@Injectable({ providedIn: 'root' })
export class DeliveryCoordinatorResolve implements Resolve<IDeliveryCoordinator> {
    constructor(private service: DeliveryCoordinatorService) {}

    resolve(route: ActivatedRouteSnapshot, state: RouterStateSnapshot): Observable<IDeliveryCoordinator> {
        const id = route.params['id'] ? route.params['id'] : null;
        if (id) {
            return this.service.find(id).pipe(
                filter((response: HttpResponse<DeliveryCoordinator>) => response.ok),
                map((deliveryCoordinator: HttpResponse<DeliveryCoordinator>) => deliveryCoordinator.body)
            );
        }
        return of(new DeliveryCoordinator());
    }
}

export const deliveryCoordinatorRoute: Routes = [
    {
        path: '',
        component: DeliveryCoordinatorComponent,
        data: {
            authorities: ['ROLE_USER'],
            pageTitle: 'imputationSqliApp.deliveryCoordinator.home.title'
        },
        canActivate: [UserRouteAccessService]
    },
    {
        path: ':id/view',
        component: DeliveryCoordinatorDetailComponent,
        resolve: {
            deliveryCoordinator: DeliveryCoordinatorResolve
        },
        data: {
            authorities: ['ROLE_USER'],
            pageTitle: 'imputationSqliApp.deliveryCoordinator.home.title'
        },
        canActivate: [UserRouteAccessService]
    },
    {
        path: 'new',
        component: DeliveryCoordinatorUpdateComponent,
        resolve: {
            deliveryCoordinator: DeliveryCoordinatorResolve
        },
        data: {
            authorities: ['ROLE_USER'],
            pageTitle: 'imputationSqliApp.deliveryCoordinator.home.title'
        },
        canActivate: [UserRouteAccessService]
    },
    {
        path: ':id/edit',
        component: DeliveryCoordinatorUpdateComponent,
        resolve: {
            deliveryCoordinator: DeliveryCoordinatorResolve
        },
        data: {
            authorities: ['ROLE_USER'],
            pageTitle: 'imputationSqliApp.deliveryCoordinator.home.title'
        },
        canActivate: [UserRouteAccessService]
    }
];

export const deliveryCoordinatorPopupRoute: Routes = [
    {
        path: ':id/delete',
        component: DeliveryCoordinatorDeletePopupComponent,
        resolve: {
            deliveryCoordinator: DeliveryCoordinatorResolve
        },
        data: {
            authorities: ['ROLE_USER'],
            pageTitle: 'imputationSqliApp.deliveryCoordinator.home.title'
        },
        canActivate: [UserRouteAccessService],
        outlet: 'popup'
    }
];
