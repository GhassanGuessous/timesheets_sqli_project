import { Component, Output, EventEmitter } from '@angular/core';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';
import { JhiEventManager } from 'ng-jhipster';

import { StateStorageService } from 'app/core/auth/state-storage.service';
import { ITbpRequestBody } from 'app/shared/model/tbp-request-body';
import { TimesheetTbpService } from 'app/entities/timesheet-tbp/timesheet-tbp.service';

@Component({
    selector: 'auth-tbp-modal',
    templateUrl: './auth-tbp.component.html'
})
export class AuthTbpModalComponent {
    tbpRequestBody: ITbpRequestBody;
    private imputations: any;

    @Output()
    passEntry: EventEmitter<any> = new EventEmitter();

    constructor(
        private eventManager: JhiEventManager,
        private stateStorageService: StateStorageService,
        public activeModal: NgbActiveModal,
        private timesheetTbpService: TimesheetTbpService
    ) {}

    authenticateToTbp() {
        this.timesheetTbpService.findTbpChargeByTeam(this.tbpRequestBody).subscribe(
            res => {
                this.imputations = res.body;
                localStorage.setItem('isTbpAuthenticated', 'true');
                localStorage.setItem('username', this.tbpRequestBody.username);
                localStorage.setItem('password', this.tbpRequestBody.password);
                this.passBack(this.imputations);
                this.activeModal.dismiss('success');
            },
            err => {
                console.log(err);
                this.activeModal.dismiss('failed');
            }
        );
    }

    passBack(data) {
        this.passEntry.emit(data);
    }
}
