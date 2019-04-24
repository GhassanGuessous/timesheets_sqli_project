import { ICollaborator } from 'app/shared/model/collaborator.model';
import { ICollaboratorDailyImputation } from 'app/shared/model/collaborator-daily-imputation.model';

export interface INotificationModel {
    collaborator?: ICollaborator;
    month?: number;
    year?: number;
    gapMap?: Map<string, ICollaboratorDailyImputation[]>;
}

export class NotificationModel implements INotificationModel {
    constructor(
        public collaborator?: ICollaborator,
        public month?: number,
        public year?: number,
        public gapMap?: Map<string, ICollaboratorDailyImputation[]>
    ) {}
}
