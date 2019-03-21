import { ICollaborator } from 'app/shared/model/collaborator.model';

export interface IActivity {
    id?: number;
    name?: string;
    collaborators?: ICollaborator[];
}

export class Activity implements IActivity {
    constructor(public id?: number, public name?: string, public collaborators?: ICollaborator[]) {}
}
