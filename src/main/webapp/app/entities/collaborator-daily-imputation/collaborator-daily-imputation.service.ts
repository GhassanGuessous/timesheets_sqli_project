import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';

import { SERVER_API_URL } from 'app/app.constants';
import { createRequestOption } from 'app/shared';
import { ICollaboratorDailyImputation } from 'app/shared/model/collaborator-daily-imputation.model';

type EntityResponseType = HttpResponse<ICollaboratorDailyImputation>;
type EntityArrayResponseType = HttpResponse<ICollaboratorDailyImputation[]>;

@Injectable({ providedIn: 'root' })
export class CollaboratorDailyImputationService {
    public resourceUrl = SERVER_API_URL + 'api/collaborator-daily-imputations';

    constructor(protected http: HttpClient) {}

    create(collaboratorDailyImputation: ICollaboratorDailyImputation): Observable<EntityResponseType> {
        return this.http.post<ICollaboratorDailyImputation>(this.resourceUrl, collaboratorDailyImputation, { observe: 'response' });
    }

    update(collaboratorDailyImputation: ICollaboratorDailyImputation): Observable<EntityResponseType> {
        return this.http.put<ICollaboratorDailyImputation>(this.resourceUrl, collaboratorDailyImputation, { observe: 'response' });
    }

    find(id: number): Observable<EntityResponseType> {
        return this.http.get<ICollaboratorDailyImputation>(`${this.resourceUrl}/${id}`, { observe: 'response' });
    }

    query(req?: any): Observable<EntityArrayResponseType> {
        const options = createRequestOption(req);
        return this.http.get<ICollaboratorDailyImputation[]>(this.resourceUrl, { params: options, observe: 'response' });
    }

    delete(id: number): Observable<HttpResponse<any>> {
        return this.http.delete<any>(`${this.resourceUrl}/${id}`, { observe: 'response' });
    }
}
