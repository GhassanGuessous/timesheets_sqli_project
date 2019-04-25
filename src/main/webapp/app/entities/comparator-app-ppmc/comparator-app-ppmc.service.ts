import { Injectable } from '@angular/core';
import { HttpClient, HttpEvent, HttpRequest, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';

import { SERVER_API_URL } from 'app/app.constants';
import { AppRequestBody } from 'app/shared/model/app-request-body';
import { IImputationComparatorDTO } from 'app/shared/model/imputation-comparator-dto.model';

@Injectable({ providedIn: 'root' })
export class ComparatorAppPpmcService {
    public resourceAppPpmcUrl = SERVER_API_URL + 'api/imputations/compare-app-ppmc';
    public resourceAppPpmcDBUrl = SERVER_API_URL + 'api/imputations/comparison-app-ppmc-database';

    constructor(protected http: HttpClient) {}

    getComparison(file: File, appRequestBody: AppRequestBody): Observable<HttpEvent<{}>> {
        const formdata: FormData = new FormData();

        formdata.append('file', file);
        formdata.append('appRequestBody', JSON.stringify(appRequestBody));
        const req = new HttpRequest('POST', this.resourceAppPpmcUrl, formdata, {
            reportProgress: true,
            responseType: 'json'
        });

        return this.http.request(req);
    }

    getComparisonFromDB(appRequestBody: AppRequestBody): Observable<HttpResponse<IImputationComparatorDTO[]>> {
        return this.http.post<any>(this.resourceAppPpmcDBUrl, appRequestBody, { observe: 'response' });
    }
}
