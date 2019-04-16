import { Injectable } from '@angular/core';
import { SERVER_API_URL } from 'app/app.constants';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { AppTbpRequestBody } from 'app/shared/model/app-tbp-request.body';
import { IImputation } from 'app/shared/model/imputation.model';
import { Observable } from 'rxjs';

type EntityResponseType = HttpResponse<any>;

@Injectable({
    providedIn: 'root'
})
export class ComparatorAPPTBPService {
    public resourceAppUrl = SERVER_API_URL + 'api/imputations/compare-app-tbp';

    constructor(protected http: HttpClient) {}

    find(id) {
        return null;
    }

    compare(appTbpRequestBody: AppTbpRequestBody): Observable<EntityResponseType> {
        console.log(appTbpRequestBody);
        return this.http.post<any>(this.resourceAppUrl, appTbpRequestBody, { observe: 'response' });
    }
}
