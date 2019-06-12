import { Injectable } from '@angular/core';
import { Resolve, ActivatedRouteSnapshot, RouterStateSnapshot, Routes } from '@angular/router';
import { JhiResolvePagingParams } from 'ng-jhipster';
import { UserRouteAccessService } from 'app/core';
import { Observable, of } from 'rxjs';
import { ITimesheetApp, TimesheetApp } from 'app/shared/model/timesheet-app.model';
import { TimesheetJiraService } from 'app/entities/timesheet-jira/timesheet-jira.service';
import { TimesheetJiraComponent } from 'app/entities/timesheet-jira/timesheet-jira.component';

@Injectable({ providedIn: 'root' })
export class TimesheetAppResolve implements Resolve<ITimesheetApp> {
    constructor(private service: TimesheetJiraService) {}

    resolve(route: ActivatedRouteSnapshot, state: RouterStateSnapshot): Observable<ITimesheetApp> {
        return of(new TimesheetApp());
    }
}

export const timesheetJiraRoute: Routes = [
    {
        path: '',
        component: TimesheetJiraComponent,
        resolve: {
            pagingParams: JhiResolvePagingParams
        },
        data: {
            authorities: ['ROLE_ADMIN', 'ROLE_DELCO'],
            defaultSort: 'id,asc',
            pageTitle: 'imputationSqliApp.imputation.home.jiraTitle'
        },
        canActivate: [UserRouteAccessService]
    }
];
