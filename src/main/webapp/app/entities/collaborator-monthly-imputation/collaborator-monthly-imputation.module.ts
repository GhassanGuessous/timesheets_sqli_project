import { NgModule, CUSTOM_ELEMENTS_SCHEMA } from '@angular/core';
import { RouterModule } from '@angular/router';
import { JhiLanguageService } from 'ng-jhipster';
import { JhiLanguageHelper } from 'app/core';

import { ImputationSqliSharedModule } from 'app/shared';
import {
    CollaboratorMonthlyImputationComponent,
    CollaboratorMonthlyImputationDetailComponent,
    CollaboratorMonthlyImputationUpdateComponent,
    CollaboratorMonthlyImputationDeletePopupComponent,
    CollaboratorMonthlyImputationDeleteDialogComponent,
    collaboratorMonthlyImputationRoute,
    collaboratorMonthlyImputationPopupRoute
} from './';

const ENTITY_STATES = [...collaboratorMonthlyImputationRoute, ...collaboratorMonthlyImputationPopupRoute];

@NgModule({
    imports: [ImputationSqliSharedModule, RouterModule.forChild(ENTITY_STATES)],
    declarations: [
        CollaboratorMonthlyImputationComponent,
        CollaboratorMonthlyImputationDetailComponent,
        CollaboratorMonthlyImputationUpdateComponent,
        CollaboratorMonthlyImputationDeleteDialogComponent,
        CollaboratorMonthlyImputationDeletePopupComponent
    ],
    entryComponents: [
        CollaboratorMonthlyImputationComponent,
        CollaboratorMonthlyImputationUpdateComponent,
        CollaboratorMonthlyImputationDeleteDialogComponent,
        CollaboratorMonthlyImputationDeletePopupComponent
    ],
    providers: [{ provide: JhiLanguageService, useClass: JhiLanguageService }],
    schemas: [CUSTOM_ELEMENTS_SCHEMA]
})
export class ImputationSqliCollaboratorMonthlyImputationModule {
    constructor(private languageService: JhiLanguageService, private languageHelper: JhiLanguageHelper) {
        this.languageHelper.language.subscribe((languageKey: string) => {
            if (languageKey !== undefined) {
                this.languageService.changeLanguage(languageKey);
            }
        });
    }
}
