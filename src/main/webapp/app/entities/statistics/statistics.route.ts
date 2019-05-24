import { Injectable } from '@angular/core';
import { Resolve, ActivatedRouteSnapshot, RouterStateSnapshot, Routes } from '@angular/router';
import { JhiPaginationUtil, JhiResolvePagingParams } from 'ng-jhipster';
import { UserRouteAccessService } from 'app/core';
import { Observable, of } from 'rxjs';
import { StatisticsService } from './statistics.service';
import { StatisticsComponent } from 'app/entities/statistics/statistics.component';
import { INotificationModel, NotificationModel } from 'app/shared/model/notification.model';

@Injectable({ providedIn: 'root' })
export class StatisticsResolve implements Resolve<INotificationModel> {
    constructor(private service: StatisticsService) {}

    resolve(route: ActivatedRouteSnapshot, state: RouterStateSnapshot): Observable<INotificationModel> {
        const id = route.params['id'] ? route.params['id'] : null;
        // if (id) {
        //     return this.service.find(id).pipe(
        //         filter((response: HttpResponse<TimesheetApp>) => response.ok),
        //         map((timesheetApp: HttpResponse<TimesheetApp>) => timesheetApp.body)
        //     );
        // }
        return of(new NotificationModel());
    }
}

export const statisticsRoute: Routes = [
    {
        path: '',
        component: StatisticsComponent,
        resolve: {
            pagingParams: JhiResolvePagingParams
        },
        data: {
            authorities: ['ROLE_ADMIN', 'ROLE_DELCO'],
            defaultSort: 'id,asc',
            pageTitle: 'imputationSqliApp.imputation.home.statisticsTitle'
        },
        canActivate: [UserRouteAccessService]
    }
];
