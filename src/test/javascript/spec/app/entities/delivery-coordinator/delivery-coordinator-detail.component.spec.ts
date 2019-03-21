/* tslint:disable max-line-length */
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';
import { of } from 'rxjs';

import { ImputationSqliTestModule } from '../../../test.module';
import { DeliveryCoordinatorDetailComponent } from 'app/entities/delivery-coordinator/delivery-coordinator-detail.component';
import { DeliveryCoordinator } from 'app/shared/model/delivery-coordinator.model';

describe('Component Tests', () => {
    describe('DeliveryCoordinator Management Detail Component', () => {
        let comp: DeliveryCoordinatorDetailComponent;
        let fixture: ComponentFixture<DeliveryCoordinatorDetailComponent>;
        const route = ({ data: of({ deliveryCoordinator: new DeliveryCoordinator(123) }) } as any) as ActivatedRoute;

        beforeEach(() => {
            TestBed.configureTestingModule({
                imports: [ImputationSqliTestModule],
                declarations: [DeliveryCoordinatorDetailComponent],
                providers: [{ provide: ActivatedRoute, useValue: route }]
            })
                .overrideTemplate(DeliveryCoordinatorDetailComponent, '')
                .compileComponents();
            fixture = TestBed.createComponent(DeliveryCoordinatorDetailComponent);
            comp = fixture.componentInstance;
        });

        describe('OnInit', () => {
            it('Should call load all on init', () => {
                // GIVEN

                // WHEN
                comp.ngOnInit();

                // THEN
                expect(comp.deliveryCoordinator).toEqual(jasmine.objectContaining({ id: 123 }));
            });
        });
    });
});
