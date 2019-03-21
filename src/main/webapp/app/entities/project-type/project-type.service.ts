import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';

import { SERVER_API_URL } from 'app/app.constants';
import { createRequestOption } from 'app/shared';
import { IProjectType } from 'app/shared/model/project-type.model';

type EntityResponseType = HttpResponse<IProjectType>;
type EntityArrayResponseType = HttpResponse<IProjectType[]>;

@Injectable({ providedIn: 'root' })
export class ProjectTypeService {
    public resourceUrl = SERVER_API_URL + 'api/project-types';

    constructor(protected http: HttpClient) {}

    create(projectType: IProjectType): Observable<EntityResponseType> {
        return this.http.post<IProjectType>(this.resourceUrl, projectType, { observe: 'response' });
    }

    update(projectType: IProjectType): Observable<EntityResponseType> {
        return this.http.put<IProjectType>(this.resourceUrl, projectType, { observe: 'response' });
    }

    find(id: number): Observable<EntityResponseType> {
        return this.http.get<IProjectType>(`${this.resourceUrl}/${id}`, { observe: 'response' });
    }

    query(req?: any): Observable<EntityArrayResponseType> {
        const options = createRequestOption(req);
        return this.http.get<IProjectType[]>(this.resourceUrl, { params: options, observe: 'response' });
    }

    delete(id: number): Observable<HttpResponse<any>> {
        return this.http.delete<any>(`${this.resourceUrl}/${id}`, { observe: 'response' });
    }
}
