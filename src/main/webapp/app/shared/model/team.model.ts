import { IDeliveryCoordinator } from 'app/shared/model/delivery-coordinator.model';
import { ICollaborator } from 'app/shared/model/collaborator.model';
import { IProject } from 'app/shared/model/project.model';

export interface ITeam {
    id?: number;
    name?: string;
    deliveryCoordinator?: IDeliveryCoordinator;
    collaborators?: ICollaborator[];
    projects?: IProject[];
}

export class Team implements ITeam {
    constructor(
        public id?: number,
        public name?: string,
        public deliveryCoordinator?: IDeliveryCoordinator,
        public collaborators?: ICollaborator[],
        public projects?: IProject[]
    ) {}
}
