import { Injectable } from '@angular/core';
import { HttpClient, HttpEvent, HttpRequest, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';

import { SERVER_API_URL } from 'app/app.constants';
import { AppRequestBody } from 'app/shared/model/app-request-body';
import { INotificationModel } from 'app/shared/model/notification.model';
import { IImputationComparatorAdvancedDTO } from 'app/shared/model/imputation-comparator-advanced-dto.model';

@Injectable({ providedIn: 'root' })
export class ComparatorAppPpmcAdvancedService {
    public resourceAppPpmcUrl = SERVER_API_URL + 'api/imputations/compare-app-ppmc-advanced';
    public resourceAppPpmcDBUrl = SERVER_API_URL + 'api/imputations/comparison-app-ppmc-advanced-database';
    public resourceNotificationUrl = SERVER_API_URL + 'api/imputations/notify';

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

    getAdvancedComparisonFromDB(appRequestBody: AppRequestBody): Observable<HttpResponse<IImputationComparatorAdvancedDTO[]>> {
        return this.http.post<any>(this.resourceAppPpmcDBUrl, appRequestBody, { observe: 'response' });
    }

    sendNotifications(notifications: INotificationModel[]): Observable<HttpResponse<any>> {
        return this.http.post<any>(this.resourceNotificationUrl, notifications, { observe: 'response' });
    }
}
