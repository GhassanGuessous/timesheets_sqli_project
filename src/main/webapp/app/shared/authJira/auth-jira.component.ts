import { Component, Output, EventEmitter } from '@angular/core';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';
import { JhiEventManager } from 'ng-jhipster';

import { StateStorageService } from 'app/core/auth/state-storage.service';
import { TimesheetJiraService } from 'app/entities/timesheet-jira/timesheet-jira.service';
import { PpmcProjectsWorklogedStatisticsService } from 'app/entities/ppmc-projects-workloged-statistics/ppmc-projects-workloged-statistics.service';
import { IssueTypeStatisticsService } from 'app/entities/issue-type-statistics/issue-type-statistics.service';

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
        private timesheetJiraService: TimesheetJiraService,
        private ppmcProjectsWorklogedStatisticsService: PpmcProjectsWorklogedStatisticsService,
        private issueTypeStatisticsService: IssueTypeStatisticsService
    ) {}

    authenticateToJiraAndGetImputations() {
        this.sendRequest().subscribe(
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

    private sendRequest() {
        if (this.requestBody.requestType === 'JIRA_TIME_SHEET') {
            return this.timesheetJiraService.getTimesheet(this.requestBody);
        } else if (this.requestBody.requestType === 'JIRA_ISSUE_TYPE_STATISTICS') {
            return this.issueTypeStatisticsService.getIssueTypeStatistics(this.requestBody);
        } else {
            return this.ppmcProjectsWorklogedStatisticsService.getPpmcProjetctsWorklog(this.requestBody);
        }
    }
}
