import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';

import { SERVER_API_URL } from 'app/app.constants';
import { ITeamYearRequest } from 'app/shared/model/team-year-request.model';

type EntityResponseType = HttpResponse<any>;

@Injectable({ providedIn: 'root' })
export class StatisticsService {
    public resourceStatisticsUrl = SERVER_API_URL + 'api/imputations/statistics';
    public resourceGapVariationUrl = SERVER_API_URL + 'api/imputations/gap-variation-statistics';

    constructor(protected http: HttpClient) {}

    findTeamNotifications(teamYearRequest: ITeamYearRequest): Observable<EntityResponseType> {
        return this.http.post<any>(this.resourceStatisticsUrl, teamYearRequest, { observe: 'response' });
    }

    getNotificationsGapVariation(teamYearRequest: ITeamYearRequest): Observable<EntityResponseType> {
        return this.http.post<any>(this.resourceGapVariationUrl, teamYearRequest, { observe: 'response' });
    }
}
