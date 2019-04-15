import { Injectable } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { Resolve, ActivatedRouteSnapshot, RouterStateSnapshot, Routes } from '@angular/router';
import { JhiResolvePagingParams } from 'ng-jhipster';
import { UserRouteAccessService } from 'app/core';
import { Observable, of } from 'rxjs';
import { filter, map } from 'rxjs/operators';
import { ITimesheetApp, TimesheetApp } from 'app/shared/model/timesheet-app.model';
import { ComparatorAPPTBPService } from 'app/entities/comparator-app-tbp/comparator-app-tbp.service';
import { ComparatorAPPTBPComponent } from 'app/entities/comparator-app-tbp/comparator-app-tbp.component';

@Injectable({ providedIn: 'root' })
export class TimesheetAppResolve implements Resolve<ITimesheetApp> {
    constructor(private service: ComparatorAPPTBPService) {}

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

export const comparatorAppTbpRoute: Routes = [
    {
        path: '',
        component: ComparatorAPPTBPComponent,
        resolve: {
            pagingParams: JhiResolvePagingParams
        },
        data: {
            authorities: ['ROLE_ADMIN', 'ROLE_DELCO'],
            defaultSort: 'id,asc',
            pageTitle: 'imputationSqliApp.imputation.home.APPvsTBP-title'
        },
        canActivate: [UserRouteAccessService]
    }
];
