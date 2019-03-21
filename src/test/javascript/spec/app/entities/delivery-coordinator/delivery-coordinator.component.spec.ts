/* tslint:disable max-line-length */
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { Observable, of } from 'rxjs';
import { HttpHeaders, HttpResponse } from '@angular/common/http';

import { ImputationSqliTestModule } from '../../../test.module';
import { DeliveryCoordinatorComponent } from 'app/entities/delivery-coordinator/delivery-coordinator.component';
import { DeliveryCoordinatorService } from 'app/entities/delivery-coordinator/delivery-coordinator.service';
import { DeliveryCoordinator } from 'app/shared/model/delivery-coordinator.model';

describe('Component Tests', () => {
    describe('DeliveryCoordinator Management Component', () => {
        let comp: DeliveryCoordinatorComponent;
        let fixture: ComponentFixture<DeliveryCoordinatorComponent>;
        let service: DeliveryCoordinatorService;

        beforeEach(() => {
            TestBed.configureTestingModule({
                imports: [ImputationSqliTestModule],
                declarations: [DeliveryCoordinatorComponent],
                providers: []
            })
                .overrideTemplate(DeliveryCoordinatorComponent, '')
                .compileComponents();

            fixture = TestBed.createComponent(DeliveryCoordinatorComponent);
            comp = fixture.componentInstance;
            service = fixture.debugElement.injector.get(DeliveryCoordinatorService);
        });

        it('Should call load all on init', () => {
            // GIVEN
            const headers = new HttpHeaders().append('link', 'link;link');
            spyOn(service, 'query').and.returnValue(
                of(
                    new HttpResponse({
                        body: [new DeliveryCoordinator(123)],
                        headers
                    })
                )
            );

            // WHEN
            comp.ngOnInit();

            // THEN
            expect(service.query).toHaveBeenCalled();
            expect(comp.deliveryCoordinators[0]).toEqual(jasmine.objectContaining({ id: 123 }));
        });
    });
});
