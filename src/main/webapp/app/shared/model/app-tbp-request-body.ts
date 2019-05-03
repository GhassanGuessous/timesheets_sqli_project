import { ITeam } from 'app/shared/model/team.model';

export interface IAppTbpRequestBody {
    team?: ITeam;
    year?: number;
    month?: number;
    username?: string;
    password?: string;
    requestType?: string;
}

export class AppTbpRequestBody implements IAppTbpRequestBody {
    constructor(
        public team?: ITeam,
        public year?: number,
        public month?: number,
        public username?: string,
        public password?: string,
        requestType?: string
    ) {}
}
