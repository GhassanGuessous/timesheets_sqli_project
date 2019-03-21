import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';

import { SERVER_API_URL } from 'app/app.constants';
import { createRequestOption } from 'app/shared';
import { ICollaboratorMonthlyImputation } from 'app/shared/model/collaborator-monthly-imputation.model';

type EntityResponseType = HttpResponse<ICollaboratorMonthlyImputation>;
type EntityArrayResponseType = HttpResponse<ICollaboratorMonthlyImputation[]>;

@Injectable({ providedIn: 'root' })
export class CollaboratorMonthlyImputationService {
    public resourceUrl = SERVER_API_URL + 'api/collaborator-monthly-imputations';

    constructor(protected http: HttpClient) {}

    create(collaboratorMonthlyImputation: ICollaboratorMonthlyImputation): Observable<EntityResponseType> {
        return this.http.post<ICollaboratorMonthlyImputation>(this.resourceUrl, collaboratorMonthlyImputation, { observe: 'response' });
    }

    update(collaboratorMonthlyImputation: ICollaboratorMonthlyImputation): Observable<EntityResponseType> {
        return this.http.put<ICollaboratorMonthlyImputation>(this.resourceUrl, collaboratorMonthlyImputation, { observe: 'response' });
    }

    find(id: number): Observable<EntityResponseType> {
        return this.http.get<ICollaboratorMonthlyImputation>(`${this.resourceUrl}/${id}`, { observe: 'response' });
    }

    query(req?: any): Observable<EntityArrayResponseType> {
        const options = createRequestOption(req);
        return this.http.get<ICollaboratorMonthlyImputation[]>(this.resourceUrl, { params: options, observe: 'response' });
    }

    delete(id: number): Observable<HttpResponse<any>> {
        return this.http.delete<any>(`${this.resourceUrl}/${id}`, { observe: 'response' });
    }
}
