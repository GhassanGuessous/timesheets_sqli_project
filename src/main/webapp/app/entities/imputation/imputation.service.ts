import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';

import { SERVER_API_URL } from 'app/app.constants';
import { createRequestOption } from 'app/shared';
import { IImputation } from 'app/shared/model/imputation.model';

type EntityResponseType = HttpResponse<IImputation>;
type EntityArrayResponseType = HttpResponse<IImputation[]>;

@Injectable({ providedIn: 'root' })
export class ImputationService {
    public resourceUrl = SERVER_API_URL + 'api/imputations';

    constructor(protected http: HttpClient) {}

    create(imputation: IImputation): Observable<EntityResponseType> {
        return this.http.post<IImputation>(this.resourceUrl, imputation, { observe: 'response' });
    }

    update(imputation: IImputation): Observable<EntityResponseType> {
        return this.http.put<IImputation>(this.resourceUrl, imputation, { observe: 'response' });
    }

    find(id: number): Observable<EntityResponseType> {
        return this.http.get<IImputation>(`${this.resourceUrl}/${id}`, { observe: 'response' });
    }

    query(req?: any): Observable<EntityArrayResponseType> {
        const options = createRequestOption(req);
        return this.http.get<IImputation[]>(this.resourceUrl, { params: options, observe: 'response' });
    }

    delete(id: number): Observable<HttpResponse<any>> {
        return this.http.delete<any>(`${this.resourceUrl}/${id}`, { observe: 'response' });
    }
}
