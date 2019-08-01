import { ITeam } from 'app/shared/model/team.model';
import { IAppTbpIdentifier } from 'app/shared/model/app-tbp-identifiers.model';

export interface IAppTbpRequestBody {
    team?: ITeam;
    year?: number;
    month?: number;
    username?: string;
    password?: string;
    requestType?: string;
    appTbpIdentifier?: IAppTbpIdentifier;
}

export class AppTbpRequestBody implements IAppTbpRequestBody {
    constructor(
        public team?: ITeam,
        public year?: number,
        public month?: number,
        public username?: string,
        public password?: string,
        public requestType?: string,
        public appTbpIdentifier?: IAppTbpIdentifier
    ) {}
}
