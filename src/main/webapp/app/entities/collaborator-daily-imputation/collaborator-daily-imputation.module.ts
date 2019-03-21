import { NgModule, CUSTOM_ELEMENTS_SCHEMA } from '@angular/core';
import { RouterModule } from '@angular/router';
import { JhiLanguageService } from 'ng-jhipster';
import { JhiLanguageHelper } from 'app/core';

import { ImputationSqliSharedModule } from 'app/shared';
import {
    CollaboratorDailyImputationComponent,
    CollaboratorDailyImputationDetailComponent,
    CollaboratorDailyImputationUpdateComponent,
    CollaboratorDailyImputationDeletePopupComponent,
    CollaboratorDailyImputationDeleteDialogComponent,
    collaboratorDailyImputationRoute,
    collaboratorDailyImputationPopupRoute
} from './';

const ENTITY_STATES = [...collaboratorDailyImputationRoute, ...collaboratorDailyImputationPopupRoute];

@NgModule({
    imports: [ImputationSqliSharedModule, RouterModule.forChild(ENTITY_STATES)],
    declarations: [
        CollaboratorDailyImputationComponent,
        CollaboratorDailyImputationDetailComponent,
        CollaboratorDailyImputationUpdateComponent,
        CollaboratorDailyImputationDeleteDialogComponent,
        CollaboratorDailyImputationDeletePopupComponent
    ],
    entryComponents: [
        CollaboratorDailyImputationComponent,
        CollaboratorDailyImputationUpdateComponent,
        CollaboratorDailyImputationDeleteDialogComponent,
        CollaboratorDailyImputationDeletePopupComponent
    ],
    providers: [{ provide: JhiLanguageService, useClass: JhiLanguageService }],
    schemas: [CUSTOM_ELEMENTS_SCHEMA]
})
export class ImputationSqliCollaboratorDailyImputationModule {
    constructor(private languageService: JhiLanguageService, private languageHelper: JhiLanguageHelper) {
        this.languageHelper.language.subscribe((languageKey: string) => {
            if (languageKey !== undefined) {
                this.languageService.changeLanguage(languageKey);
            }
        });
    }
}
