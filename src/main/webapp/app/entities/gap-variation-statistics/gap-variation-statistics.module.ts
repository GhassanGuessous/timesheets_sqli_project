import { NgModule, CUSTOM_ELEMENTS_SCHEMA } from '@angular/core';
import { RouterModule } from '@angular/router';
import { JhiLanguageService } from 'ng-jhipster';
import { JhiLanguageHelper } from 'app/core';

import { ImputationSqliSharedModule } from 'app/shared';
import { GapVariationStatisticsComponent } from 'app/entities/gap-variation-statistics/gap-variation-statistics.component';
import { gapVariationStatisticsRoute } from 'app/entities/gap-variation-statistics/gap-variation-statistics.route';

const ENTITY_STATES = [...gapVariationStatisticsRoute];

@NgModule({
    imports: [ImputationSqliSharedModule, RouterModule.forChild(ENTITY_STATES)],
    declarations: [GapVariationStatisticsComponent],
    entryComponents: [GapVariationStatisticsComponent],
    providers: [{ provide: JhiLanguageService, useClass: JhiLanguageService }],
    schemas: [CUSTOM_ELEMENTS_SCHEMA]
})
export class GapVariationStatisticsModule {
    constructor(private languageService: JhiLanguageService, private languageHelper: JhiLanguageHelper) {
        this.languageHelper.language.subscribe((languageKey: string) => {
            if (languageKey !== undefined) {
                this.languageService.changeLanguage(languageKey);
            }
        });
    }
}
