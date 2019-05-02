export interface ITbpRequestBody {
    idTbp?: string;
    startDate?: string;
    endDate?: string;
    username?: string;
    password?: string;
}

export class TbpRequestBody implements ITbpRequestBody {
    constructor(
        public idTbp?: string,
        public startDate?: string,
        public endDate?: string,
        public username?: string,
        public password?: string
    ) {}
}
