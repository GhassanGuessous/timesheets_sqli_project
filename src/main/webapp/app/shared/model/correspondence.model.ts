import { ICollaborator } from 'app/shared/model/collaborator.model';

export interface ICorrespondence {
    id?: number;
    idAPP?: string;
    idPPMC?: string;
    idTBP?: string;
    collaborator?: ICollaborator;
}

export class Correspondence implements ICorrespondence {
    constructor(
        public id?: number,
        public idAPP?: string,
        public idPPMC?: string,
        public idTBP?: string,
        public collaborator?: ICollaborator
    ) {}
}
