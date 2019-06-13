import { NgModule, CUSTOM_ELEMENTS_SCHEMA } from '@angular/core';
import { RouterModule } from '@angular/router';
import { JhiLanguageService } from 'ng-jhipster';
import { JhiLanguageHelper } from 'app/core';

import { ImputationSqliSharedModule } from 'app/shared';
import { gapPerTeamStatisticsRoute } from 'app/entities/gap-per-team-statistics/gap-per-team-statistics.route';
import { GapPerTeamStatisticsComponent } from 'app/entities/gap-per-team-statistics/gap-per-team-statistics.component';

const ENTITY_STATES = [...gapPerTeamStatisticsRoute];

@NgModule({
    imports: [ImputationSqliSharedModule, RouterModule.forChild(ENTITY_STATES)],
    declarations: [GapPerTeamStatisticsComponent],
    entryComponents: [GapPerTeamStatisticsComponent],
    providers: [{ provide: JhiLanguageService, useClass: JhiLanguageService }],
    schemas: [CUSTOM_ELEMENTS_SCHEMA]
})
export class GapPerTeamStatisticsModule {
    constructor(private languageService: JhiLanguageService, private languageHelper: JhiLanguageHelper) {
        this.languageHelper.language.subscribe((languageKey: string) => {
            if (languageKey !== undefined) {
                this.languageService.changeLanguage(languageKey);
            }
        });
    }
}
