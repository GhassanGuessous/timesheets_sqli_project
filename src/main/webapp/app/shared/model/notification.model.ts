import { ICollaborator } from 'app/shared/model/collaborator.model';
import { ICollaboratorDailyImputation } from 'app/shared/model/collaborator-daily-imputation.model';

export interface INotificationModel {
    collaborator?: ICollaborator;
    gapMap?: Map<string, ICollaboratorDailyImputation[]>;
}

export class NotificationModel implements INotificationModel {
    constructor(public collaborator?: ICollaborator, public gapMap?: Map<string, ICollaboratorDailyImputation[]>) {}
}
