import { NgModule, CUSTOM_ELEMENTS_SCHEMA } from '@angular/core';
import { RouterModule } from '@angular/router';
import { JhiLanguageService } from 'ng-jhipster';
import { JhiLanguageHelper } from 'app/core';

import { ImputationSqliSharedModule } from 'app/shared';
import {
    ImputationTypeComponent,
    ImputationTypeDetailComponent,
    ImputationTypeUpdateComponent,
    ImputationTypeDeletePopupComponent,
    ImputationTypeDeleteDialogComponent,
    imputationTypeRoute,
    imputationTypePopupRoute
} from './';

const ENTITY_STATES = [...imputationTypeRoute, ...imputationTypePopupRoute];

@NgModule({
    imports: [ImputationSqliSharedModule, RouterModule.forChild(ENTITY_STATES)],
    declarations: [
        ImputationTypeComponent,
        ImputationTypeDetailComponent,
        ImputationTypeUpdateComponent,
        ImputationTypeDeleteDialogComponent,
        ImputationTypeDeletePopupComponent
    ],
    entryComponents: [
        ImputationTypeComponent,
        ImputationTypeUpdateComponent,
        ImputationTypeDeleteDialogComponent,
        ImputationTypeDeletePopupComponent
    ],
    providers: [{ provide: JhiLanguageService, useClass: JhiLanguageService }],
    schemas: [CUSTOM_ELEMENTS_SCHEMA]
})
export class ImputationSqliImputationTypeModule {
    constructor(private languageService: JhiLanguageService, private languageHelper: JhiLanguageHelper) {
        this.languageHelper.language.subscribe((languageKey: string) => {
            if (languageKey !== undefined) {
                this.languageService.changeLanguage(languageKey);
            }
        });
    }
}
