import { CUSTOM_ELEMENTS_SCHEMA, NgModule } from '@angular/core';
import { JhiLanguageService } from 'ng-jhipster';
import { JhiLanguageHelper } from 'app/core';
import { ImputationSqliSharedModule } from 'app/shared';
import { RouterModule } from '@angular/router';
import { comparatorAppTbpRoute } from 'app/entities/comparator-app-tbp/comparator-app-tbp.route';
import { ComparatorAPPTBPComponent } from 'app/entities/comparator-app-tbp/comparator-app-tbp.component';

const ENTITY_STATES = [...comparatorAppTbpRoute];

@NgModule({
    imports: [ImputationSqliSharedModule, RouterModule.forChild(ENTITY_STATES)],
    declarations: [ComparatorAPPTBPComponent],
    entryComponents: [ComparatorAPPTBPComponent],
    providers: [{ provide: JhiLanguageService, useClass: JhiLanguageService }],
    schemas: [CUSTOM_ELEMENTS_SCHEMA]
})
export class ComparatorAPPTBPModule {
    constructor(private languageService: JhiLanguageService, private languageHelper: JhiLanguageHelper) {
        this.languageHelper.language.subscribe((languageKey: string) => {
            if (languageKey !== undefined) {
                this.languageService.changeLanguage(languageKey);
            }
        });
    }
}
