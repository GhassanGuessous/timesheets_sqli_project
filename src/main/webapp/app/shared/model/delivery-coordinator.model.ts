export interface IDeliveryCoordinator {
    id?: number;
    firstName?: string;
    lastName?: string;
    login?: string;
}

export class DeliveryCoordinator implements IDeliveryCoordinator {
    constructor(public id?: number, public firstName?: string, public lastName?: string, public login?: string) {}
}
