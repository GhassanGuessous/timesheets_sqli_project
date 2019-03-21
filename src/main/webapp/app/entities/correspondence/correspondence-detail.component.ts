import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';

import { ICorrespondence } from 'app/shared/model/correspondence.model';

@Component({
    selector: 'jhi-correspondence-detail',
    templateUrl: './correspondence-detail.component.html'
})
export class CorrespondenceDetailComponent implements OnInit {
    correspondence: ICorrespondence;

    constructor(protected activatedRoute: ActivatedRoute) {}

    ngOnInit() {
        this.activatedRoute.data.subscribe(({ correspondence }) => {
            this.correspondence = correspondence;
        });
    }

    previousState() {
        window.history.back();
    }
}
