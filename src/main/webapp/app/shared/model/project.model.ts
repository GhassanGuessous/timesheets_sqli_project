import { ITeam } from 'app/shared/model/team.model';
import { IProjectType } from 'app/shared/model/project-type.model';

export interface IProject {
    id?: number;
    agresso?: string;
    name?: string;
    teams?: ITeam[];
    projectType?: IProjectType;
}

export class Project implements IProject {
    constructor(
        public id?: number,
        public agresso?: string,
        public name?: string,
        public teams?: ITeam[],
        public projectType?: IProjectType
    ) {}
}
