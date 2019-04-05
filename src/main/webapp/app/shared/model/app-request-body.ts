export interface IAppRequestBody {
    agresso?: string;
    startDate?: string;
    endDate?: string;
}

export class AppRequestBody implements IAppRequestBody {
    constructor(public agresso?: string, public startDate?: string, public endDate?: string) {}
}
