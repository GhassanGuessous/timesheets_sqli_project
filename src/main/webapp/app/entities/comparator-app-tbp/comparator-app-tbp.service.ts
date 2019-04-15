import { Injectable } from '@angular/core';
import { SERVER_API_URL } from 'app/app.constants';
import { HttpClient } from '@angular/common/http';

@Injectable({
    providedIn: 'root'
})
export class ComparatorAPPTBPService {
    public resourceAppUrl = SERVER_API_URL + 'api/imputations/comparator-app-tbp';

    constructor(protected http: HttpClient) {}

    find(id) {
        return null;
    }
}
