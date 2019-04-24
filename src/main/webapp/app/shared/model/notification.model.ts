import { ICollaborator } from 'app/shared/model/collaborator.model';
import { IGapModel } from 'app/shared/model/gap.model';

export interface INotificationModel {
    collaborator?: ICollaborator;
    month?: number;
    year?: number;
    appGap?: IGapModel;
    comparedGap?: IGapModel;
}

export class NotificationModel implements INotificationModel {
    constructor(
        public collaborator?: ICollaborator,
        public month?: number,
        public year?: number,
        public appGap?: IGapModel,
        public comparedGap?: IGapModel
    ) {}
}
