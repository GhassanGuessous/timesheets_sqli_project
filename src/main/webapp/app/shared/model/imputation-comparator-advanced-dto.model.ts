import { ICollaborator } from 'app/shared/model/collaborator.model';
import { ICollaboratorMonthlyImputation } from 'app/shared/model/collaborator-monthly-imputation.model';

export interface IImputationComparatorAdvancedDTO {
    collaborator?: ICollaborator;
    appMonthlyImputation?: ICollaboratorMonthlyImputation;
    comparedMonthlyImputation?: ICollaboratorMonthlyImputation;
}

export class ImputationComparatorAdvancedDTO implements IImputationComparatorAdvancedDTO {
    constructor(
        public collaborator?: ICollaborator,
        public appMonthlyImputation?: ICollaboratorMonthlyImputation,
        public comparedMonthlyImputation?: ICollaboratorMonthlyImputation
    ) {}
}
