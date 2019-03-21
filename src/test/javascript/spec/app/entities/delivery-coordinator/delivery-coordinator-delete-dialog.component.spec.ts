/* tslint:disable max-line-length */
import { ComponentFixture, TestBed, inject, fakeAsync, tick } from '@angular/core/testing';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';
import { Observable, of } from 'rxjs';
import { JhiEventManager } from 'ng-jhipster';

import { ImputationSqliTestModule } from '../../../test.module';
import { DeliveryCoordinatorDeleteDialogComponent } from 'app/entities/delivery-coordinator/delivery-coordinator-delete-dialog.component';
import { DeliveryCoordinatorService } from 'app/entities/delivery-coordinator/delivery-coordinator.service';

describe('Component Tests', () => {
    describe('DeliveryCoordinator Management Delete Component', () => {
        let comp: DeliveryCoordinatorDeleteDialogComponent;
        let fixture: ComponentFixture<DeliveryCoordinatorDeleteDialogComponent>;
        let service: DeliveryCoordinatorService;
        let mockEventManager: any;
        let mockActiveModal: any;

        beforeEach(() => {
            TestBed.configureTestingModule({
                imports: [ImputationSqliTestModule],
                declarations: [DeliveryCoordinatorDeleteDialogComponent]
            })
                .overrideTemplate(DeliveryCoordinatorDeleteDialogComponent, '')
                .compileComponents();
            fixture = TestBed.createComponent(DeliveryCoordinatorDeleteDialogComponent);
            comp = fixture.componentInstance;
            service = fixture.debugElement.injector.get(DeliveryCoordinatorService);
            mockEventManager = fixture.debugElement.injector.get(JhiEventManager);
            mockActiveModal = fixture.debugElement.injector.get(NgbActiveModal);
        });

        describe('confirmDelete', () => {
            it('Should call delete service on confirmDelete', inject(
                [],
                fakeAsync(() => {
                    // GIVEN
                    spyOn(service, 'delete').and.returnValue(of({}));

                    // WHEN
                    comp.confirmDelete(123);
                    tick();

                    // THEN
                    expect(service.delete).toHaveBeenCalledWith(123);
                    expect(mockActiveModal.dismissSpy).toHaveBeenCalled();
                    expect(mockEventManager.broadcastSpy).toHaveBeenCalled();
                })
            ));
        });
    });
});
