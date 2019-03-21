import { NgModule, CUSTOM_ELEMENTS_SCHEMA } from '@angular/core';
import { RouterModule } from '@angular/router';
import { JhiLanguageService } from 'ng-jhipster';
import { JhiLanguageHelper } from 'app/core';

import { ImputationSqliSharedModule } from 'app/shared';
import {
    ProjectTypeComponent,
    ProjectTypeDetailComponent,
    ProjectTypeUpdateComponent,
    ProjectTypeDeletePopupComponent,
    ProjectTypeDeleteDialogComponent,
    projectTypeRoute,
    projectTypePopupRoute
} from './';

const ENTITY_STATES = [...projectTypeRoute, ...projectTypePopupRoute];

@NgModule({
    imports: [ImputationSqliSharedModule, RouterModule.forChild(ENTITY_STATES)],
    declarations: [
        ProjectTypeComponent,
        ProjectTypeDetailComponent,
        ProjectTypeUpdateComponent,
        ProjectTypeDeleteDialogComponent,
        ProjectTypeDeletePopupComponent
    ],
    entryComponents: [ProjectTypeComponent, ProjectTypeUpdateComponent, ProjectTypeDeleteDialogComponent, ProjectTypeDeletePopupComponent],
    providers: [{ provide: JhiLanguageService, useClass: JhiLanguageService }],
    schemas: [CUSTOM_ELEMENTS_SCHEMA]
})
export class ImputationSqliProjectTypeModule {
    constructor(private languageService: JhiLanguageService, private languageHelper: JhiLanguageHelper) {
        this.languageHelper.language.subscribe((languageKey: string) => {
            if (languageKey !== undefined) {
                this.languageService.changeLanguage(languageKey);
            }
        });
    }
}
