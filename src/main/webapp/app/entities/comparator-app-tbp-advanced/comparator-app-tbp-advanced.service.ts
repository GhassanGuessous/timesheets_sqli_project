import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { SERVER_API_URL } from 'app/app.constants';
import { AppTbpRequestBody } from 'app/shared/model/app-tbp-request-body';
import { Observable } from 'rxjs';
import { INotificationModel } from 'app/shared/model/notification.model';

type EntityResponseType = HttpResponse<any>;

@Injectable({
    providedIn: 'root'
})
export class ComparatorAppTbpAdvancedService {
    public resourceAppUrl = SERVER_API_URL + 'api/imputations/compare-app-tbp-advanced';
    public resourceNotificationUrl = SERVER_API_URL + 'api/imputations/notify';

    constructor(protected http: HttpClient) {}

    find(id) {
        return null;
    }

    compare(appTbpRequestBody: AppTbpRequestBody): Observable<EntityResponseType> {
        return this.http.post<any>(this.resourceAppUrl, appTbpRequestBody, { observe: 'response' });
    }

    sendNotifications(notifications: INotificationModel[]): Observable<HttpResponse<any>> {
        return this.http.post<any>(this.resourceNotificationUrl, notifications, { observe: 'response' });
    }
}
