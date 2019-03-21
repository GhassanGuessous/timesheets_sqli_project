import { ITeam } from 'app/shared/model/team.model';
import { ICollaboratorMonthlyImputation } from 'app/shared/model/collaborator-monthly-imputation.model';
import { IActivity } from 'app/shared/model/activity.model';

export interface ICollaborator {
    id?: number;
    firstname?: string;
    lastname?: string;
    email?: string;
    team?: ITeam;
    monthlyImputations?: ICollaboratorMonthlyImputation[];
    activity?: IActivity;
}

export class Collaborator implements ICollaborator {
    constructor(
        public id?: number,
        public firstname?: string,
        public lastname?: string,
        public email?: string,
        public team?: ITeam,
        public monthlyImputations?: ICollaboratorMonthlyImputation[],
        public activity?: IActivity
    ) {}
}
