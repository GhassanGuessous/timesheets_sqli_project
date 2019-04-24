import { ICollaboratorDailyImputation } from 'app/shared/model/collaborator-daily-imputation.model';

export interface IGapModel {
    imputationType?: string;
    dailyImputations?: ICollaboratorDailyImputation[];
}

export class GapModel implements IGapModel {
    constructor(public imputationType?: string, public dailyImputations?: ICollaboratorDailyImputation[]) {}
}
