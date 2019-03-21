import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';

import { SERVER_API_URL } from 'app/app.constants';
import { createRequestOption } from 'app/shared';
import { IDeliveryCoordinator } from 'app/shared/model/delivery-coordinator.model';

type EntityResponseType = HttpResponse<IDeliveryCoordinator>;
type EntityArrayResponseType = HttpResponse<IDeliveryCoordinator[]>;

@Injectable({ providedIn: 'root' })
export class DeliveryCoordinatorService {
    public resourceUrl = SERVER_API_URL + 'api/delivery-coordinators';

    constructor(protected http: HttpClient) {}

    create(deliveryCoordinator: IDeliveryCoordinator): Observable<EntityResponseType> {
        return this.http.post<IDeliveryCoordinator>(this.resourceUrl, deliveryCoordinator, { observe: 'response' });
    }

    update(deliveryCoordinator: IDeliveryCoordinator): Observable<EntityResponseType> {
        return this.http.put<IDeliveryCoordinator>(this.resourceUrl, deliveryCoordinator, { observe: 'response' });
    }

    find(id: number): Observable<EntityResponseType> {
        return this.http.get<IDeliveryCoordinator>(`${this.resourceUrl}/${id}`, { observe: 'response' });
    }

    query(req?: any): Observable<EntityArrayResponseType> {
        const options = createRequestOption(req);
        return this.http.get<IDeliveryCoordinator[]>(this.resourceUrl, { params: options, observe: 'response' });
    }

    delete(id: number): Observable<HttpResponse<any>> {
        return this.http.delete<any>(`${this.resourceUrl}/${id}`, { observe: 'response' });
    }
}
