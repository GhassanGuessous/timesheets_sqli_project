import { CUSTOM_ELEMENTS_SCHEMA, NgModule } from '@angular/core';
import { ImputationSqliSharedModule } from 'app/shared';
import { RouterModule } from '@angular/router';
import { JhiLanguageService } from 'ng-jhipster';
import { ComparatorAppTbpAdvancedComponent } from 'app/entities/comparator-app-tbp-advanced/comparator-app-tbp-advanced.component';
import { comparatorAppTbpAdvancedRoute } from 'app/entities/comparator-app-tbp-advanced/comparator-app-tbp-advanced.route';
import { JhiLanguageHelper } from 'app/core';

const ENTITY_STATES = [...comparatorAppTbpAdvancedRoute];

@NgModule({
    imports: [ImputationSqliSharedModule, RouterModule.forChild(ENTITY_STATES)],
    declarations: [ComparatorAppTbpAdvancedComponent],
    entryComponents: [ComparatorAppTbpAdvancedComponent],
    providers: [{ provide: JhiLanguageService, useClass: JhiLanguageService }],
    schemas: [CUSTOM_ELEMENTS_SCHEMA]
})
export class ComparatorAppTbpAdvancedModule {
    constructor(private languageService: JhiLanguageService, private languageHelper: JhiLanguageHelper) {
        this.languageHelper.language.subscribe((languageKey: string) => {
            if (languageKey !== undefined) {
                this.languageService.changeLanguage(languageKey);
            }
        });
    }
}
