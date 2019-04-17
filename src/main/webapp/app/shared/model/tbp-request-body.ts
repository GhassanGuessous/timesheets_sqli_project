export interface ITbpRequestBody {
    idTbp?: string;
    startDate?: string;
    endDate?: string;
}

export class TbpRequestBody implements ITbpRequestBody {
    constructor(public idTbp?: string, public startDate?: string, public endDate?: string) {}
}
