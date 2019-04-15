import { NgModule, CUSTOM_ELEMENTS_SCHEMA } from '@angular/core';
import { RouterModule } from '@angular/router';
import { JhiLanguageService } from 'ng-jhipster';
import { JhiLanguageHelper } from 'app/core';

import { ImputationSqliSharedModule } from 'app/shared';
import { comparatorAppPpmcRoute } from 'app/entities/comparator-app-ppmc/comparator-app-ppmc.route';
import { ComparatorAppPpmcComponent } from 'app/entities/comparator-app-ppmc/comparator-app-ppmc.component';

const ENTITY_STATES = [...comparatorAppPpmcRoute];

@NgModule({
    imports: [ImputationSqliSharedModule, RouterModule.forChild(ENTITY_STATES)],
    declarations: [ComparatorAppPpmcComponent],
    entryComponents: [ComparatorAppPpmcComponent],
    providers: [{ provide: JhiLanguageService, useClass: JhiLanguageService }],
    schemas: [CUSTOM_ELEMENTS_SCHEMA]
})
export class ComparatorAppPpmcModule {
    constructor(private languageService: JhiLanguageService, private languageHelper: JhiLanguageHelper) {
        this.languageHelper.language.subscribe((languageKey: string) => {
            if (languageKey !== undefined) {
                this.languageService.changeLanguage(languageKey);
            }
        });
    }
}
