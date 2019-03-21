import { NgModule, CUSTOM_ELEMENTS_SCHEMA } from '@angular/core';
import { RouterModule } from '@angular/router';
import { JhiLanguageService } from 'ng-jhipster';
import { JhiLanguageHelper } from 'app/core';

import { ImputationSqliSharedModule } from 'app/shared';
import {
    DeliveryCoordinatorComponent,
    DeliveryCoordinatorDetailComponent,
    DeliveryCoordinatorUpdateComponent,
    DeliveryCoordinatorDeletePopupComponent,
    DeliveryCoordinatorDeleteDialogComponent,
    deliveryCoordinatorRoute,
    deliveryCoordinatorPopupRoute
} from './';

const ENTITY_STATES = [...deliveryCoordinatorRoute, ...deliveryCoordinatorPopupRoute];

@NgModule({
    imports: [ImputationSqliSharedModule, RouterModule.forChild(ENTITY_STATES)],
    declarations: [
        DeliveryCoordinatorComponent,
        DeliveryCoordinatorDetailComponent,
        DeliveryCoordinatorUpdateComponent,
        DeliveryCoordinatorDeleteDialogComponent,
        DeliveryCoordinatorDeletePopupComponent
    ],
    entryComponents: [
        DeliveryCoordinatorComponent,
        DeliveryCoordinatorUpdateComponent,
        DeliveryCoordinatorDeleteDialogComponent,
        DeliveryCoordinatorDeletePopupComponent
    ],
    providers: [{ provide: JhiLanguageService, useClass: JhiLanguageService }],
    schemas: [CUSTOM_ELEMENTS_SCHEMA]
})
export class ImputationSqliDeliveryCoordinatorModule {
    constructor(private languageService: JhiLanguageService, private languageHelper: JhiLanguageHelper) {
        this.languageHelper.language.subscribe((languageKey: string) => {
            if (languageKey !== undefined) {
                this.languageService.changeLanguage(languageKey);
            }
        });
    }
}
