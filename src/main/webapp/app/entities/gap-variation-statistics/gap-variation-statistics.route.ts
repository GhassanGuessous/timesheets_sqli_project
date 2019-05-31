import { Injectable } from '@angular/core';
import { Resolve, ActivatedRouteSnapshot, RouterStateSnapshot, Routes } from '@angular/router';
import { JhiResolvePagingParams } from 'ng-jhipster';
import { UserRouteAccessService } from 'app/core';
import { Observable, of } from 'rxjs';
import { INotificationModel, NotificationModel } from 'app/shared/model/notification.model';
import { StatisticsService } from '../statistics/statistics.service';
import { GapVariationStatisticsComponent } from './gap-variation-statistics.component';

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

export const gapVariationStatisticsRoute: Routes = [
    {
        path: '',
        component: GapVariationStatisticsComponent,
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
