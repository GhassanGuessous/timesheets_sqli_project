import { ITeam } from 'app/shared/model/team.model';

export interface IAppTbpRequestBody {
    team?: ITeam;
    year?: number;
    month?: number;
}

export class AppTbpRequestBody implements IAppTbpRequestBody {
    constructor(public team?: ITeam, public year?: number, public month?: number) {}
}
