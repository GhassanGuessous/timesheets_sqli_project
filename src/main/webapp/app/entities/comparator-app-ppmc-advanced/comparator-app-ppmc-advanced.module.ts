import { NgModule, CUSTOM_ELEMENTS_SCHEMA } from '@angular/core';
import { RouterModule } from '@angular/router';
import { JhiLanguageService } from 'ng-jhipster';
import { JhiLanguageHelper } from 'app/core';

import { ImputationSqliSharedModule } from 'app/shared';
import { comparatorAppPpmcAdvancedRoute } from 'app/entities/comparator-app-ppmc-advanced/comparator-app-ppmc-advanced.route';
import { ComparatorAppPpmcAdvancedComponent } from 'app/entities/comparator-app-ppmc-advanced/comparator-app-ppmc-advanced.component';

const ENTITY_STATES = [...comparatorAppPpmcAdvancedRoute];

@NgModule({
    imports: [ImputationSqliSharedModule, RouterModule.forChild(ENTITY_STATES)],
    declarations: [ComparatorAppPpmcAdvancedComponent],
    entryComponents: [ComparatorAppPpmcAdvancedComponent],
    providers: [{ provide: JhiLanguageService, useClass: JhiLanguageService }],
    schemas: [CUSTOM_ELEMENTS_SCHEMA]
})
export class ComparatorAppPpmcAdvancedModule {
    constructor(private languageService: JhiLanguageService, private languageHelper: JhiLanguageHelper) {
        this.languageHelper.language.subscribe((languageKey: string) => {
            if (languageKey !== undefined) {
                this.languageService.changeLanguage(languageKey);
            }
        });
    }
}
