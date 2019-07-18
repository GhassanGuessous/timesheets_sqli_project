export interface IProjectType {
    id?: number;
    name?: string;
}

export class ProjectType implements IProjectType {
    constructor(public id?: number, public name?: string) {}
}
