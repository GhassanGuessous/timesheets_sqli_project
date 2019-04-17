import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';

import { SERVER_API_URL } from 'app/app.constants';
import { IImputation } from 'app/shared/model/imputation.model';
import { ITbpRequestBody } from 'app/shared/model/tbp-request-body';

type EntityResponseType = HttpResponse<IImputation>;

@Injectable({ providedIn: 'root' })
export class TimesheetTbpService {
    public resourceUrl = SERVER_API_URL + 'api/imputations/tbp';

    constructor(protected http: HttpClient) {}

    findTbpChargeByTeam(tbpRequestBody: ITbpRequestBody): Observable<EntityResponseType> {
        return this.http.post<any>(this.resourceUrl, tbpRequestBody, { observe: 'response' });
    }
}
