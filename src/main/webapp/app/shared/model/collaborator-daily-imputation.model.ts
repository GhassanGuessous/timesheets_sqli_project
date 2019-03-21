import { ICollaboratorMonthlyImputation } from 'app/shared/model/collaborator-monthly-imputation.model';

export interface ICollaboratorDailyImputation {
    id?: number;
    day?: number;
    charge?: number;
    collaboratorMonthlyImputation?: ICollaboratorMonthlyImputation;
}

export class CollaboratorDailyImputation implements ICollaboratorDailyImputation {
    constructor(
        public id?: number,
        public day?: number,
        public charge?: number,
        public collaboratorMonthlyImputation?: ICollaboratorMonthlyImputation
    ) {}
}
