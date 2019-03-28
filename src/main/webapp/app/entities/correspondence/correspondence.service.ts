import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';

import { SERVER_API_URL } from 'app/app.constants';
import { createRequestOption } from 'app/shared';
import { ICorrespondence } from 'app/shared/model/correspondence.model';

type EntityResponseType = HttpResponse<ICorrespondence>;
type EntityArrayResponseType = HttpResponse<ICorrespondence[]>;

@Injectable({ providedIn: 'root' })
export class CorrespondenceService {
    public resourceUrl = SERVER_API_URL + 'api/correspondences';

    constructor(protected http: HttpClient) {}

    create(correspondence: ICorrespondence): Observable<EntityResponseType> {
        return this.http.post<ICorrespondence>(this.resourceUrl, correspondence, { observe: 'response' });
    }

    update(correspondence: ICorrespondence): Observable<EntityResponseType> {
        return this.http.put<ICorrespondence>(this.resourceUrl, correspondence, { observe: 'response' });
    }

    find(id: number): Observable<EntityResponseType> {
        return this.http.get<ICorrespondence>(`${this.resourceUrl}/${id}`, { observe: 'response' });
    }

    query(req?: any): Observable<EntityArrayResponseType> {
        const options = createRequestOption(req);
        return this.http.get<ICorrespondence[]>(this.resourceUrl, { params: options, observe: 'response' });
    }

    delete(id: number): Observable<HttpResponse<any>> {
        return this.http.delete<any>(`${this.resourceUrl}/${id}`, { observe: 'response' });
    }

    searchedQuery(key: string, req?: any): Observable<EntityArrayResponseType> {
        const options = createRequestOption(req);
        return this.http.get<ICorrespondence[]>(`${this.resourceUrl}/search/${key}`, { params: options, observe: 'response' });
    }
}
