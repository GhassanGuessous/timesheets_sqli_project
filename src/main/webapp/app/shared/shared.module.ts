import { NgModule, CUSTOM_ELEMENTS_SCHEMA } from '@angular/core';
import { NgbDateAdapter } from '@ng-bootstrap/ng-bootstrap';

import { NgbDateMomentAdapter } from './util/datepicker-adapter';
import { ImputationSqliSharedLibsModule, ImputationSqliSharedCommonModule, JhiLoginModalComponent, HasAnyAuthorityDirective } from './';
import { AuthTbpModalComponent } from 'app/shared/authTbp/auth-tbp.component';

@NgModule({
    imports: [ImputationSqliSharedLibsModule, ImputationSqliSharedCommonModule],
    declarations: [JhiLoginModalComponent, HasAnyAuthorityDirective, AuthTbpModalComponent],
    providers: [{ provide: NgbDateAdapter, useClass: NgbDateMomentAdapter }],
    entryComponents: [JhiLoginModalComponent, AuthTbpModalComponent],
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
