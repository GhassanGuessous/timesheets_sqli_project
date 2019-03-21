import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';

import { SERVER_API_URL } from 'app/app.constants';
import { createRequestOption } from 'app/shared';
import { IImputationType } from 'app/shared/model/imputation-type.model';

type EntityResponseType = HttpResponse<IImputationType>;
type EntityArrayResponseType = HttpResponse<IImputationType[]>;

@Injectable({ providedIn: 'root' })
export class ImputationTypeService {
    public resourceUrl = SERVER_API_URL + 'api/imputation-types';

    constructor(protected http: HttpClient) {}

    create(imputationType: IImputationType): Observable<EntityResponseType> {
        return this.http.post<IImputationType>(this.resourceUrl, imputationType, { observe: 'response' });
    }

    update(imputationType: IImputationType): Observable<EntityResponseType> {
        return this.http.put<IImputationType>(this.resourceUrl, imputationType, { observe: 'response' });
    }

    find(id: number): Observable<EntityResponseType> {
        return this.http.get<IImputationType>(`${this.resourceUrl}/${id}`, { observe: 'response' });
    }

    query(req?: any): Observable<EntityArrayResponseType> {
        const options = createRequestOption(req);
        return this.http.get<IImputationType[]>(this.resourceUrl, { params: options, observe: 'response' });
    }

    delete(id: number): Observable<HttpResponse<any>> {
        return this.http.delete<any>(`${this.resourceUrl}/${id}`, { observe: 'response' });
    }
}
