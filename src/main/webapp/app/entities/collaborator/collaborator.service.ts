import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';

import { SERVER_API_URL } from 'app/app.constants';
import { createRequestOption } from 'app/shared';
import { ICollaborator } from 'app/shared/model/collaborator.model';

type EntityResponseType = HttpResponse<ICollaborator>;
type EntityArrayResponseType = HttpResponse<ICollaborator[]>;

@Injectable({ providedIn: 'root' })
export class CollaboratorService {
    public resourceUrl = SERVER_API_URL + 'api/collaborators';

    constructor(protected http: HttpClient) {}

    create(collaborator: ICollaborator): Observable<EntityResponseType> {
        return this.http.post<ICollaborator>(this.resourceUrl, collaborator, { observe: 'response' });
    }

    update(collaborator: ICollaborator): Observable<EntityResponseType> {
        return this.http.put<ICollaborator>(this.resourceUrl, collaborator, { observe: 'response' });
    }

    find(id: number): Observable<EntityResponseType> {
        return this.http.get<ICollaborator>(`${this.resourceUrl}/${id}`, { observe: 'response' });
    }

    query(req?: any): Observable<EntityArrayResponseType> {
        const options = createRequestOption(req);
        return this.http.get<ICollaborator[]>(this.resourceUrl, { params: options, observe: 'response' });
    }

    delete(id: number): Observable<HttpResponse<any>> {
        return this.http.delete<any>(`${this.resourceUrl}/${id}`, { observe: 'response' });
    }

    searchedQuery(key: string, req?: any): Observable<EntityArrayResponseType> {
        const options = createRequestOption(req);
        return this.http.get<ICollaborator[]>(`${this.resourceUrl}/search/${key}`, { params: options, observe: 'response' });
    }
}
