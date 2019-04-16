import { ICollaborator } from 'app/shared/model/collaborator.model';

export interface IAppTbpComparator {
    collaborator?: ICollaborator;
    totalApp?: number;
    totalTbp?: number;
    difference?: number;
}

export class AppTbpComparator implements IAppTbpComparator {
    constructor(public collaborator?: ICollaborator, public totalApp?: number, public totalTbp?: number, public difference?: number) {
        this.difference = this.totalApp - this.totalTbp;
    }
}
