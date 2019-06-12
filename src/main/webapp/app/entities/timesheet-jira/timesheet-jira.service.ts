import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';

import { SERVER_API_URL } from 'app/app.constants';
import { AppTbpRequestBody } from 'app/shared/model/app-tbp-request-body';

type EntityResponseType = HttpResponse<any>;

@Injectable({ providedIn: 'root' })
export class TimesheetJiraService {
    public resourceAppUrl = SERVER_API_URL + 'api/imputations/jira';

    constructor(protected http: HttpClient) {}

    find(id) {
        return null;
    }

    getTimesheet(appTbpRequestBody: AppTbpRequestBody): Observable<EntityResponseType> {
        return this.http.post<any>(this.resourceAppUrl, appTbpRequestBody, { observe: 'response' });
    }
}
