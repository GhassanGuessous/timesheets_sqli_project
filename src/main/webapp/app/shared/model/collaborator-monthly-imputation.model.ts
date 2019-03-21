import { ICollaborator } from 'app/shared/model/collaborator.model';
import { IImputation } from 'app/shared/model/imputation.model';
import { ICollaboratorDailyImputation } from 'app/shared/model/collaborator-daily-imputation.model';

export interface ICollaboratorMonthlyImputation {
    id?: number;
    total?: number;
    collaborator?: ICollaborator;
    imputation?: IImputation;
    dailyImputations?: ICollaboratorDailyImputation[];
}

export class CollaboratorMonthlyImputation implements ICollaboratorMonthlyImputation {
    constructor(
        public id?: number,
        public total?: number,
        public collaborator?: ICollaborator,
        public imputation?: IImputation,
        public dailyImputations?: ICollaboratorDailyImputation[]
    ) {}
}
