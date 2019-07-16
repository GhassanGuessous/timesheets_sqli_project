export interface IAppTbpIdentifiers {
    id?: string;
    agresso?: string;
    idTbp?: string;
}

export class AppTbpIdentifiers implements IAppTbpIdentifiers {
    constructor(public id?: string, public agresso?: string, public idTbp?: string) {}
}
