import { NgModule, CUSTOM_ELEMENTS_SCHEMA } from '@angular/core';
import { RouterModule } from '@angular/router';
import { JhiLanguageService } from 'ng-jhipster';
import { JhiLanguageHelper } from 'app/core';

import { ImputationSqliSharedModule } from 'app/shared';
import { timesheetTbpRoute } from 'app/entities/timesheet-tbp/timesheet-tbp.route';
import { TimesheetTbpComponent } from 'app/entities/timesheet-tbp/timesheet-tbp.component';

const ENTITY_STATES = [...timesheetTbpRoute];

@NgModule({
    imports: [ImputationSqliSharedModule, RouterModule.forChild(ENTITY_STATES)],
    declarations: [TimesheetTbpComponent],
    entryComponents: [TimesheetTbpComponent],
    providers: [{ provide: JhiLanguageService, useClass: JhiLanguageService }],
    schemas: [CUSTOM_ELEMENTS_SCHEMA]
})
export class ImputationSqliTimesheetTbpModule {
    constructor(private languageService: JhiLanguageService, private languageHelper: JhiLanguageHelper) {
        this.languageHelper.language.subscribe((languageKey: string) => {
            if (languageKey !== undefined) {
                this.languageService.changeLanguage(languageKey);
            }
        });
    }
}
