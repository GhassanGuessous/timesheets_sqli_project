export interface IDeliveryCoordinator {
    id?: number;
}

export class DeliveryCoordinator implements IDeliveryCoordinator {
    constructor(public id?: number) {}
}
