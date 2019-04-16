import { ICollaborator } from 'app/shared/model/collaborator.model';

export interface IAppPpmcComparator {
    collaborator?: ICollaborator;
    totalApp?: number;
    totalPpmc?: number;
    difference?: number;
}

export class AppPpmcComparator implements IAppPpmcComparator {
    constructor(public collaborator?: ICollaborator, public totalApp?: number, public totalPpmc?: number, public difference?: number) {}
}
