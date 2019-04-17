import { Injectable } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { Resolve, ActivatedRouteSnapshot, RouterStateSnapshot, Routes } from '@angular/router';
import { JhiPaginationUtil, JhiResolvePagingParams } from 'ng-jhipster';
import { UserRouteAccessService } from 'app/core';
import { Observable, of } from 'rxjs';
import { filter, map } from 'rxjs/operators';
import { ComparatorAppPpmcService } from './comparator-app-ppmc.service';
import { ComparatorAppPpmcComponent } from './comparator-app-ppmc.component';
import { ITimesheetApp, TimesheetApp } from 'app/shared/model/timesheet-app.model';

@Injectable({ providedIn: 'root' })
export class ComparatorAppPpmcResolve implements Resolve<ITimesheetApp> {
    constructor(private service: ComparatorAppPpmcService) {}

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

export const comparatorAppPpmcRoute: Routes = [
    {
        path: '',
        component: ComparatorAppPpmcComponent,
        resolve: {
            pagingParams: JhiResolvePagingParams
        },
        data: {
            authorities: ['ROLE_ADMIN', 'ROLE_DELCO'],
            defaultSort: 'id,asc',
            pageTitle: 'imputationSqliApp.imputation.home.comparatorAppPpmcTitle'
        },
        canActivate: [UserRouteAccessService]
    }
];
