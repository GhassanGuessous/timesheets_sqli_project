import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';

import { SERVER_API_URL } from 'app/app.constants';
import { IAppRequestBody } from 'app/shared/model/app-request.body';
import { IImputation } from 'app/shared/model/imputation.model';

type EntityResponseType = HttpResponse<IImputation>;

@Injectable({ providedIn: 'root' })
export class TimesheetAppService {
    public resourceAppUrl = SERVER_API_URL + 'api/imputations/app';

    constructor(protected http: HttpClient) {}

    findAppChargeByTeam(appRequestBody: IAppRequestBody): Observable<EntityResponseType> {
        return this.http.post<any>(this.resourceAppUrl, appRequestBody, { observe: 'response' });
    }
}
