import { NgModule, CUSTOM_ELEMENTS_SCHEMA } from '@angular/core';
import { RouterModule } from '@angular/router';
import { JhiLanguageService } from 'ng-jhipster';
import { JhiLanguageHelper } from 'app/core';

import { ImputationSqliSharedModule } from 'app/shared';
import {
    CollaboratorComponent,
    CollaboratorDetailComponent,
    CollaboratorUpdateComponent,
    CollaboratorDeletePopupComponent,
    CollaboratorDeleteDialogComponent,
    collaboratorRoute,
    collaboratorPopupRoute
} from './';

const ENTITY_STATES = [...collaboratorRoute, ...collaboratorPopupRoute];

@NgModule({
    imports: [ImputationSqliSharedModule, RouterModule.forChild(ENTITY_STATES)],
    declarations: [
        CollaboratorComponent,
        CollaboratorDetailComponent,
        CollaboratorUpdateComponent,
        CollaboratorDeleteDialogComponent,
        CollaboratorDeletePopupComponent
    ],
    entryComponents: [
        CollaboratorComponent,
        CollaboratorUpdateComponent,
        CollaboratorDeleteDialogComponent,
        CollaboratorDeletePopupComponent
    ],
    providers: [{ provide: JhiLanguageService, useClass: JhiLanguageService }],
    schemas: [CUSTOM_ELEMENTS_SCHEMA]
})
export class ImputationSqliCollaboratorModule {
    constructor(private languageService: JhiLanguageService, private languageHelper: JhiLanguageHelper) {
        this.languageHelper.language.subscribe((languageKey: string) => {
            if (languageKey !== undefined) {
                this.languageService.changeLanguage(languageKey);
            }
        });
    }
}
