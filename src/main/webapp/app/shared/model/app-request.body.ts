export interface IAppRequestBody {
    agresso?: string;
    year?: number;
    month?: number;
    startDay?: number;
    manDay?: number;
}

export class AppRequestBody implements IAppRequestBody {
    constructor(public agresso?: string, public year?: number, public month?: number, public startDay?: number, public manDay?: number) {}
}
