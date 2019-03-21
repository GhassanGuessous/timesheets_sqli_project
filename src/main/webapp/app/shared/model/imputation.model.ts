import { ICollaboratorMonthlyImputation } from 'app/shared/model/collaborator-monthly-imputation.model';
import { IImputationType } from 'app/shared/model/imputation-type.model';

export interface IImputation {
    id?: number;
    month?: number;
    year?: number;
    monthlyImputations?: ICollaboratorMonthlyImputation[];
    imputationType?: IImputationType;
}

export class Imputation implements IImputation {
    constructor(
        public id?: number,
        public month?: number,
        public year?: number,
        public monthlyImputations?: ICollaboratorMonthlyImputation[],
        public imputationType?: IImputationType
    ) {}
}
