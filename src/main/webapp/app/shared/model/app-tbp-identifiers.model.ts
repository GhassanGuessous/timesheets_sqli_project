export interface IAppTbpIdentifier {
    id?: string;
    agresso?: string;
    idTbp?: string;
    mission?: string;
}

export class AppTbpIdentifier implements IAppTbpIdentifier {
    constructor(public id?: string, public agresso?: string, public idTbp?: string, public miission?: string) {}
}
