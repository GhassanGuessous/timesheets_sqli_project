import { NgModule, CUSTOM_ELEMENTS_SCHEMA } from '@angular/core';
import { NgbDateAdapter } from '@ng-bootstrap/ng-bootstrap';

import { NgbDateMomentAdapter } from './util/datepicker-adapter';
import { ImputationSqliSharedLibsModule, ImputationSqliSharedCommonModule, JhiLoginModalComponent, HasAnyAuthorityDirective } from './';
import { AuthTbpModalComponent } from 'app/shared/authTbp/auth-tbp.component';
import { AuthJiraModalComponent } from 'app/shared/authJira/auth-jira.component';

@NgModule({
    imports: [ImputationSqliSharedLibsModule, ImputationSqliSharedCommonModule],
    declarations: [JhiLoginModalComponent, HasAnyAuthorityDirective, AuthTbpModalComponent, AuthJiraModalComponent],
    providers: [{ provide: NgbDateAdapter, useClass: NgbDateMomentAdapter }],
    entryComponents: [JhiLoginModalComponent, AuthTbpModalComponent, AuthJiraModalComponent],
    exports: [ImputationSqliSharedCommonModule, JhiLoginModalComponent, HasAnyAuthorityDirective],
    schemas: [CUSTOM_ELEMENTS_SCHEMA]
})
export class ImputationSqliSharedModule {
    static forRoot() {
        return {
            ngModule: ImputationSqliSharedModule
        };
    }
}
