import { Component, Output, EventEmitter } from '@angular/core';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';
import { JhiEventManager } from 'ng-jhipster';

import { StateStorageService } from 'app/core/auth/state-storage.service';
import { TimesheetJiraService } from 'app/entities/timesheet-jira/timesheet-jira.service';

@Component({
    selector: 'jhi-auth-jira-modal',
    templateUrl: './auth-jira.component.html'
})
export class AuthJiraModalComponent {
    requestBody: any;

    @Output()
    private passEntry: EventEmitter<any> = new EventEmitter();

    constructor(
        private eventManager: JhiEventManager,
        private stateStorageService: StateStorageService,
        public activeModal: NgbActiveModal,
        private timesheetJiraService: TimesheetJiraService
    ) {}

    authenticateToJiraAndGetImputations() {
        this.getIpmutations().subscribe(
            res => {
                localStorage.setItem('isJiraAuthenticated', 'true');
                localStorage.setItem('jiraUsername', this.requestBody.username);
                localStorage.setItem('jiraPassword', this.requestBody.password);
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
        return this.timesheetJiraService.getTimesheet(this.requestBody);
    }
}
