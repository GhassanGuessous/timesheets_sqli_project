import { IProject } from 'app/shared/model/project.model';

export interface IProjectType {
    id?: number;
    name?: string;
    projects?: IProject[];
}

export class ProjectType implements IProjectType {
    constructor(public id?: number, public name?: string, public projects?: IProject[]) {}
}
