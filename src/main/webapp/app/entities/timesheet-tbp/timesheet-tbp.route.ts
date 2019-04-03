import { Injectable } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { Resolve, ActivatedRouteSnapshot, RouterStateSnapshot, Routes } from '@angular/router';
import { JhiPaginationUtil, JhiResolvePagingParams } from 'ng-jhipster';
import { UserRouteAccessService } from 'app/core';
import { Observable, of } from 'rxjs';
import { filter, map } from 'rxjs/operators';
import { TimesheetTbpComponent } from 'app/entities/timesheet-tbp/timesheet-tbp.component';
import { TimesheetTbpService } from 'app/entities/timesheet-tbp/timesheet-tbp.service';
import { ITimesheetTbp, TimesheetTbp } from 'app/shared/model/timesheet-tbp.model';

@Injectable({ providedIn: 'root' })
export class TimesheetTbpResolve implements Resolve<ITimesheetTbp> {
    constructor(private service: TimesheetTbpService) {}

    resolve(route: ActivatedRouteSnapshot, state: RouterStateSnapshot): Observable<ITimesheetTbp> {
        const id = route.params['id'] ? route.params['id'] : null;
        if (id) {
            return this.service.find(id).pipe(
                filter((response: HttpResponse<TimesheetTbp>) => response.ok),
                map((timesheetTbp: HttpResponse<TimesheetTbp>) => timesheetTbp.body)
            );
        }
        return of(new TimesheetTbp());
    }
}

export const timesheetTbpRoute: Routes = [
    {
        path: '',
        component: TimesheetTbpComponent,
        resolve: {
            pagingParams: JhiResolvePagingParams
        },
        data: {
            authorities: ['ROLE_ADMIN', 'ROLE_DELCO'],
            defaultSort: 'id,asc',
            pageTitle: 'imputationSqliApp.activity.home.title'
        },
        canActivate: [UserRouteAccessService]
    }
];
