import { Injectable } from '@angular/core';
import { Resolve, ActivatedRouteSnapshot, RouterStateSnapshot, Routes } from '@angular/router';
import { JhiResolvePagingParams } from 'ng-jhipster';
import { UserRouteAccessService } from 'app/core';
import { Observable, of } from 'rxjs';
import { PpmcProjectsWorklogedStatisticsService } from 'app/entities/ppmc-projects-workloged-statistics/ppmc-projects-workloged-statistics.service';
import { PpmcProjectsWorklogedStatisticsComponent } from 'app/entities/ppmc-projects-workloged-statistics/ppmc-projects-workloged-statistics.component';

@Injectable({ providedIn: 'root' })
export class StatisticsResolve implements Resolve<any> {
    constructor(private service: PpmcProjectsWorklogedStatisticsService) {}

    resolve(route: ActivatedRouteSnapshot, state: RouterStateSnapshot): Observable<any> {
        const id = route.params['id'] ? route.params['id'] : null;
        // if (id) {
        //     return this.service.find(id).pipe(
        //         filter((response: HttpResponse<TimesheetApp>) => response.ok),
        //         map((timesheetApp: HttpResponse<TimesheetApp>) => timesheetApp.body)
        //     );
        // }
        return of({});
    }
}

export const ppmcProjectsWorklogedStatisticsRoute: Routes = [
    {
        path: '',
        component: PpmcProjectsWorklogedStatisticsComponent,
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
