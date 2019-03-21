import { NgModule, CUSTOM_ELEMENTS_SCHEMA } from '@angular/core';
import { RouterModule } from '@angular/router';
import { JhiLanguageService } from 'ng-jhipster';
import { JhiLanguageHelper } from 'app/core';

import { ImputationSqliSharedModule } from 'app/shared';
import {
    CorrespondenceComponent,
    CorrespondenceDetailComponent,
    CorrespondenceUpdateComponent,
    CorrespondenceDeletePopupComponent,
    CorrespondenceDeleteDialogComponent,
    correspondenceRoute,
    correspondencePopupRoute
} from './';

const ENTITY_STATES = [...correspondenceRoute, ...correspondencePopupRoute];

@NgModule({
    imports: [ImputationSqliSharedModule, RouterModule.forChild(ENTITY_STATES)],
    declarations: [
        CorrespondenceComponent,
        CorrespondenceDetailComponent,
        CorrespondenceUpdateComponent,
        CorrespondenceDeleteDialogComponent,
        CorrespondenceDeletePopupComponent
    ],
    entryComponents: [
        CorrespondenceComponent,
        CorrespondenceUpdateComponent,
        CorrespondenceDeleteDialogComponent,
        CorrespondenceDeletePopupComponent
    ],
    providers: [{ provide: JhiLanguageService, useClass: JhiLanguageService }],
    schemas: [CUSTOM_ELEMENTS_SCHEMA]
})
export class ImputationSqliCorrespondenceModule {
    constructor(private languageService: JhiLanguageService, private languageHelper: JhiLanguageHelper) {
        this.languageHelper.language.subscribe((languageKey: string) => {
            if (languageKey !== undefined) {
                this.languageService.changeLanguage(languageKey);
            }
        });
    }
}
