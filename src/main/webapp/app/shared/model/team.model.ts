import { IDeliveryCoordinator } from 'app/shared/model/delivery-coordinator.model';
import { ICollaborator } from 'app/shared/model/collaborator.model';
import { IProjectType } from 'app/shared/model/project-type.model';
import { IAppTbpIdentifiers } from 'app/shared/model/app-tbp-identifiers.model';

export interface ITeam {
    id?: number;
    name?: string;
    displayName?: string;
    agresso?: string;
    projectType?: IProjectType;
    deliveryCoordinator?: IDeliveryCoordinator;
    collaborators?: ICollaborator[];
    idTbp?: string;
    appTbpIdentifiers?: IAppTbpIdentifiers[];
}

export class Team implements ITeam {
    constructor(
        public id?: number,
        public name?: string,
        public displayName?: string,
        public deliveryCoordinator?: IDeliveryCoordinator,
        public collaborators?: ICollaborator[],
        public projectType?: IProjectType,
        public idTbp?: string,
        public appTbpIdentifiers?: IAppTbpIdentifiers[]
    ) {}
}
