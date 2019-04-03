import { NgModule, CUSTOM_ELEMENTS_SCHEMA } from '@angular/core';
import { RouterModule } from '@angular/router';
import { JhiLanguageService } from 'ng-jhipster';
import { JhiLanguageHelper } from 'app/core';

import { ImputationSqliSharedModule } from 'app/shared';
import { timesheetAppRoute } from 'app/entities/timesheet-app/timesheet-app.route';
import { TimesheetAppComponent } from 'app/entities/timesheet-app/timesheet-app.component';

const ENTITY_STATES = [...timesheetAppRoute];

@NgModule({
    imports: [ImputationSqliSharedModule, RouterModule.forChild(ENTITY_STATES)],
    declarations: [TimesheetAppComponent],
    entryComponents: [TimesheetAppComponent],
    providers: [{ provide: JhiLanguageService, useClass: JhiLanguageService }],
    schemas: [CUSTOM_ELEMENTS_SCHEMA]
})
export class ImputationSqliTimesheetAppModule {
    constructor(private languageService: JhiLanguageService, private languageHelper: JhiLanguageHelper) {
        this.languageHelper.language.subscribe((languageKey: string) => {
            if (languageKey !== undefined) {
                this.languageService.changeLanguage(languageKey);
            }
        });
    }
}
