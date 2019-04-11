import { Injectable } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { Resolve, ActivatedRouteSnapshot, RouterStateSnapshot, Routes } from '@angular/router';
import { JhiPaginationUtil, JhiResolvePagingParams } from 'ng-jhipster';
import { UserRouteAccessService } from 'app/core';
import { Observable, of } from 'rxjs';
import { filter, map } from 'rxjs/operators';
import { TimesheetPpmcComponent } from 'app/entities/timesheet-ppmc/timesheet-ppmc.component';
import { TimesheetPpmcService } from 'app/entities/timesheet-ppmc/timesheet-ppmc.service';
import { ITimesheetPpmc, TimesheetPpmc } from 'app/shared/model/timesheet-ppmc.model';

@Injectable({ providedIn: 'root' })
export class TimesheetPpmcResolve implements Resolve<ITimesheetPpmc> {
    constructor(private service: TimesheetPpmcService) {}

    resolve(route: ActivatedRouteSnapshot, state: RouterStateSnapshot): Observable<ITimesheetPpmc> {
        const id = route.params['id'] ? route.params['id'] : null;
        if (id) {
            return this.service.find(id).pipe(
                filter((response: HttpResponse<TimesheetPpmc>) => response.ok),
                map((timesheetTbp: HttpResponse<TimesheetPpmc>) => timesheetTbp.body)
            );
        }
        return of(new TimesheetPpmc());
    }
}

export const timesheetPpmcRoute: Routes = [
    {
        path: '',
        component: TimesheetPpmcComponent,
        resolve: {
            pagingParams: JhiResolvePagingParams
        },
        data: {
            authorities: ['ROLE_ADMIN', 'ROLE_DELCO'],
            defaultSort: 'id,asc',
            pageTitle: 'imputationSqliApp.imputation.home.ppmcTitle'
        },
        canActivate: [UserRouteAccessService]
    }
];
