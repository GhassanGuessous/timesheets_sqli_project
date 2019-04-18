import { Injectable } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { Resolve, ActivatedRouteSnapshot, RouterStateSnapshot, Routes } from '@angular/router';
import { JhiResolvePagingParams } from 'ng-jhipster';
import { UserRouteAccessService } from 'app/core';
import { Observable, of } from 'rxjs';
import { filter, map } from 'rxjs/operators';
import { ITimesheetApp, TimesheetApp } from 'app/shared/model/timesheet-app.model';
import { ComparatorAppTbpAdvancedService } from 'app/entities/comparator-app-tbp-advanced/comparator-app-tbp-advanced.service';
import { ComparatorAppTbpAdvancedComponent } from 'app/entities/comparator-app-tbp-advanced/comparator-app-tbp-advanced.component';

@Injectable({ providedIn: 'root' })
export class ComparatorAppTbpAdvancedResolve implements Resolve<ITimesheetApp> {
    constructor(private service: ComparatorAppTbpAdvancedService) {}

    resolve(route: ActivatedRouteSnapshot, state: RouterStateSnapshot): Observable<ITimesheetApp> {
        const id = route.params['id'] ? route.params['id'] : null;
        if (id) {
            return this.service.find(id).pipe(
                filter((response: HttpResponse<TimesheetApp>) => response.ok),
                map((timesheetApp: HttpResponse<TimesheetApp>) => timesheetApp.body)
            );
        }
        return of(new TimesheetApp());
    }
}

export const comparatorAppTbpAdvancedRoute: Routes = [
    {
        path: '',
        component: ComparatorAppTbpAdvancedComponent,
        resolve: {
            pagingParams: JhiResolvePagingParams
        },
        data: {
            authorities: ['ROLE_ADMIN', 'ROLE_DELCO'],
            defaultSort: 'id,asc',
            pageTitle: 'imputationSqliApp.imputation.home.APPvsTBP-advanced-title'
        },
        canActivate: [UserRouteAccessService]
    }
];
