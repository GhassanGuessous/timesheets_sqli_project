import { Injectable } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { Resolve, ActivatedRouteSnapshot, RouterStateSnapshot, Routes } from '@angular/router';
import { JhiPaginationUtil, JhiResolvePagingParams } from 'ng-jhipster';
import { UserRouteAccessService } from 'app/core';
import { Observable, of } from 'rxjs';
import { filter, map } from 'rxjs/operators';
import { ComparatorAppPpmcAdvancedService } from './comparator-app-ppmc-advanced.service';
import { ComparatorAppPpmcAdvancedComponent } from './comparator-app-ppmc-advanced.component';
import { ITimesheetApp, TimesheetApp } from 'app/shared/model/timesheet-app.model';

@Injectable({ providedIn: 'root' })
export class ComparatorAppPpmcAdvancedResolve implements Resolve<ITimesheetApp> {
    constructor(private service: ComparatorAppPpmcAdvancedService) {}

    resolve(route: ActivatedRouteSnapshot, state: RouterStateSnapshot): Observable<ITimesheetApp> {
        const id = route.params['id'] ? route.params['id'] : null;
        // if (id) {
        //     return this.service.find(id).pipe(
        //         filter((response: HttpResponse<TimesheetApp>) => response.ok),
        //         map((timesheetApp: HttpResponse<TimesheetApp>) => timesheetApp.body)
        //     );
        // }
        return of(new TimesheetApp());
    }
}

export const comparatorAppPpmcAdvancedRoute: Routes = [
    {
        path: '',
        component: ComparatorAppPpmcAdvancedComponent,
        resolve: {
            pagingParams: JhiResolvePagingParams
        },
        data: {
            authorities: ['ROLE_ADMIN', 'ROLE_DELCO'],
            defaultSort: 'id,asc',
            pageTitle: 'imputationSqliApp.imputation.home.comparatorAppPpmcAdvancedTitle'
        },
        canActivate: [UserRouteAccessService]
    }
];
