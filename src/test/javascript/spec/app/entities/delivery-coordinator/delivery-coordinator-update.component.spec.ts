/* tslint:disable max-line-length */
import { ComponentFixture, TestBed, fakeAsync, tick } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { Observable, of } from 'rxjs';

import { ImputationSqliTestModule } from '../../../test.module';
import { DeliveryCoordinatorUpdateComponent } from 'app/entities/delivery-coordinator/delivery-coordinator-update.component';
import { DeliveryCoordinatorService } from 'app/entities/delivery-coordinator/delivery-coordinator.service';
import { DeliveryCoordinator } from 'app/shared/model/delivery-coordinator.model';

describe('Component Tests', () => {
    describe('DeliveryCoordinator Management Update Component', () => {
        let comp: DeliveryCoordinatorUpdateComponent;
        let fixture: ComponentFixture<DeliveryCoordinatorUpdateComponent>;
        let service: DeliveryCoordinatorService;

        beforeEach(() => {
            TestBed.configureTestingModule({
                imports: [ImputationSqliTestModule],
                declarations: [DeliveryCoordinatorUpdateComponent]
            })
                .overrideTemplate(DeliveryCoordinatorUpdateComponent, '')
                .compileComponents();

            fixture = TestBed.createComponent(DeliveryCoordinatorUpdateComponent);
            comp = fixture.componentInstance;
            service = fixture.debugElement.injector.get(DeliveryCoordinatorService);
        });

        describe('save', () => {
            it('Should call update service on save for existing entity', fakeAsync(() => {
                // GIVEN
                const entity = new DeliveryCoordinator(123);
                spyOn(service, 'update').and.returnValue(of(new HttpResponse({ body: entity })));
                comp.deliveryCoordinator = entity;
                // WHEN
                comp.save();
                tick(); // simulate async

                // THEN
                expect(service.update).toHaveBeenCalledWith(entity);
                expect(comp.isSaving).toEqual(false);
            }));

            it('Should call create service on save for new entity', fakeAsync(() => {
                // GIVEN
                const entity = new DeliveryCoordinator();
                spyOn(service, 'create').and.returnValue(of(new HttpResponse({ body: entity })));
                comp.deliveryCoordinator = entity;
                // WHEN
                comp.save();
                tick(); // simulate async

                // THEN
                expect(service.create).toHaveBeenCalledWith(entity);
                expect(comp.isSaving).toEqual(false);
            }));
        });
    });
});
