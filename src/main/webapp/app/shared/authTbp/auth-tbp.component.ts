import { Component, Output, EventEmitter } from '@angular/core';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';
import { JhiEventManager } from 'ng-jhipster';

import { StateStorageService } from 'app/core/auth/state-storage.service';
import { TimesheetTbpService } from 'app/entities/timesheet-tbp/timesheet-tbp.service';
import { ComparatorAppTbpAdvancedService } from 'app/entities/comparator-app-tbp-advanced/comparator-app-tbp-advanced.service';
import { ComparatorAPPTBPService } from 'app/entities/comparator-app-tbp/comparator-app-tbp.service';

@Component({
    selector: 'jhi-auth-tbp-modal',
    templateUrl: './auth-tbp.component.html'
})
export class AuthTbpModalComponent {
    requestBody: any;

    @Output()
    passEntry: EventEmitter<any> = new EventEmitter();

    constructor(
        private eventManager: JhiEventManager,
        private stateStorageService: StateStorageService,
        public activeModal: NgbActiveModal,
        private timesheetTbpService: TimesheetTbpService,
        private comparatorAppTbpAdvancedService: ComparatorAppTbpAdvancedService,
        private comparatorAPPTBPService: ComparatorAPPTBPService
    ) {}

    authenticateToTbpAndGetImputations() {
        this.getIpmutations().subscribe(
            res => {
                localStorage.setItem('isTbpAuthenticated', 'true');
                localStorage.setItem('username', this.requestBody.username);
                localStorage.setItem('password', this.requestBody.password);
                this.passBack(res.body);
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

    private getIpmutations() {
        if (this.requestBody.requestType === 'APP_TBP_ADVANCED_COMPARATOR') {
            return this.comparatorAppTbpAdvancedService.compare(this.requestBody);
        } else if (this.requestBody.requestType === 'APP_TBP_COMPARATOR') {
            return this.comparatorAPPTBPService.compare(this.requestBody);
        } else {
            return this.timesheetTbpService.findTbpChargeByTeam(this.requestBody);
        }
    }
}
