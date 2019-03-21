import { IImputation } from 'app/shared/model/imputation.model';

export interface IImputationType {
    id?: number;
    name?: string;
    imputations?: IImputation[];
}

export class ImputationType implements IImputationType {
    constructor(public id?: number, public name?: string, public imputations?: IImputation[]) {}
}
