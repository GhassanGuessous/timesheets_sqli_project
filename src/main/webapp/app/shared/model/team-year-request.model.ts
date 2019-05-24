import { ITeam } from 'app/shared/model/team.model';

export interface ITeamYearRequest {
    team?: ITeam;
    year?: number;
}

export class TeamYearRequest implements ITeamYearRequest {
    constructor(public team?: ITeam, public year?: number) {}
}
