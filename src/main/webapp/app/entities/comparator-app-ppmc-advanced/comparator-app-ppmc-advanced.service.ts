import { Injectable } from '@angular/core';
import { HttpClient, HttpEvent, HttpRequest, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';

import { SERVER_API_URL } from 'app/app.constants';
import { AppRequestBody } from 'app/shared/model/app-request-body';

@Injectable({ providedIn: 'root' })
export class ComparatorAppPpmcAdvancedService {
    public resourceAppPpmcUrl = SERVER_API_URL + 'api/imputations/compare-app-ppmc-advanced';

    constructor(protected http: HttpClient) {}

    getAdvancedComparison(file: File, appRequestBody: AppRequestBody): Observable<HttpEvent<{}>> {
        const formdata: FormData = new FormData();

        formdata.append('file', file);
        formdata.append('appRequestBody', JSON.stringify(appRequestBody));
        const req = new HttpRequest('POST', this.resourceAppPpmcUrl, formdata, {
            reportProgress: true,
            responseType: 'json'
        });

        return this.http.request(req);
    }
}