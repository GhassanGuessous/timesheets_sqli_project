import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse, HttpRequest, HttpEvent } from '@angular/common/http';
import { Observable } from 'rxjs';

import { SERVER_API_URL } from 'app/app.constants';

@Injectable({ providedIn: 'root' })
export class TimesheetPpmcService {
    public resourceUrl = SERVER_API_URL + 'api/imputations/ppmc';

    constructor(protected http: HttpClient) {}

    // findTbpChargeByTeam(tbpRequestBody: ITbpRequestBody): Observable<EntityResponseType> {
    //     return this.http.post<any>(this.resourceUrl, tbpRequestBody, { observe: 'response' });
    // }

    pushFileToStorage(file: File): Observable<HttpEvent<{}>> {
        const formdata: FormData = new FormData();

        formdata.append('file', file);
        const req = new HttpRequest('POST', this.resourceUrl, formdata, {
            reportProgress: true,
            responseType: 'json'
        });

        return this.http.request(req);
    }

    // getFiles(): Observable<any> {
    //     return this.http.get('/getallfiles');
    // }
}