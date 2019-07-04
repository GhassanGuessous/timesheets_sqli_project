import { NgModule, CUSTOM_ELEMENTS_SCHEMA } from '@angular/core';
import { RouterModule } from '@angular/router';
import { JhiLanguageService } from 'ng-jhipster';
import { JhiLanguageHelper } from 'app/core';

import { ImputationSqliSharedModule } from 'app/shared';
import { ppmcProjectsWorklogedStatisticsRoute } from 'app/entities/ppmc-projects-workloged-statistics/ppmc-projects-workloged-statistics.route';
import { PpmcProjectsWorklogedStatisticsComponent } from 'app/entities/ppmc-projects-workloged-statistics/ppmc-projects-workloged-statistics.component';

const ENTITY_STATES = [...ppmcProjectsWorklogedStatisticsRoute];

@NgModule({
    imports: [ImputationSqliSharedModule, RouterModule.forChild(ENTITY_STATES)],
    declarations: [PpmcProjectsWorklogedStatisticsComponent],
    entryComponents: [PpmcProjectsWorklogedStatisticsComponent],
    providers: [{ provide: JhiLanguageService, useClass: JhiLanguageService }],
    schemas: [CUSTOM_ELEMENTS_SCHEMA]
})
export class PpmcProjectsWorklogedStatisticsModule {
    constructor(private languageService: JhiLanguageService, private languageHelper: JhiLanguageHelper) {
        this.languageHelper.language.subscribe((languageKey: string) => {
            if (languageKey !== undefined) {
                this.languageService.changeLanguage(languageKey);
            }
        });
    }
}
