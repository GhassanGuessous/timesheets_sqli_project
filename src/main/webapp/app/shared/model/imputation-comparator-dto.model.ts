import { ICollaborator } from 'app/shared/model/collaborator.model';
import { ICollaboratorMonthlyImputation } from 'app/shared/model/collaborator-monthly-imputation.model';

export interface IImputationComparatorDTO {
    collaborator?: ICollaborator;
    totalApp?: number;
    totalCompared?: number;
    difference?: number;
}

export class ImputationComparatorDTO implements IImputationComparatorDTO {
    constructor(public collaborator?: ICollaborator, public totalApp?: number, public totalCompared?: number, public difference?: number) {}
}
