import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';

import { SERVER_API_URL } from 'app/app.constants';
import { ITbpRequestBody } from 'app/shared/model/tbp-request-body';

type EntityResponseType = HttpResponse<any>;

@Injectable({ providedIn: 'root' })
export class PpmcProjectsWorklogedStatisticsService {
    public resourceStatisticsUrl = SERVER_API_URL + 'api/imputations/ppmc-project-worklogs-statistics';

    constructor(protected http: HttpClient) {}

    getPpmcProjetctsWorklog(requestBody: ITbpRequestBody): Observable<EntityResponseType> {
        return this.http.post<any>(this.resourceStatisticsUrl, requestBody, { observe: 'response' });
    }
}
