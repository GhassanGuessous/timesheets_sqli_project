import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse, HttpRequest, HttpEvent } from '@angular/common/http';
import { Observable } from 'rxjs';

import { SERVER_API_URL } from 'app/app.constants';
import { AppRequestBody } from 'app/shared/model/app-request-body';

@Injectable({ providedIn: 'root' })
export class TimesheetPpmcService {
    public resourceUrl = SERVER_API_URL + 'api/imputations/ppmc';
    public resourcePpmcDBUrl = SERVER_API_URL + 'api/imputations/ppmc-database';

    constructor(protected http: HttpClient) {}

    getPpmcTimeSheet(file: File, appRequestBody: AppRequestBody): Observable<HttpEvent<{}>> {
        const formdata: FormData = new FormData();

        formdata.append('file', file);
        formdata.append('appRequestBody', JSON.stringify(appRequestBody));
        const req = new HttpRequest('POST', this.resourceUrl, formdata, {
            reportProgress: true,
            responseType: 'json'
        });

        return this.http.request(req);
    }

    getPpmcTimeSheetFromDB(appRequestBody: AppRequestBody) {
        return this.http.post<any>(this.resourcePpmcDBUrl, appRequestBody, { observe: 'response' });
    }
}
