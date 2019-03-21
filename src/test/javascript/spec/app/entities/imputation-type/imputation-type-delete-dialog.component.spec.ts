/* tslint:disable max-line-length */
import { ComponentFixture, TestBed, inject, fakeAsync, tick } from '@angular/core/testing';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';
import { Observable, of } from 'rxjs';
import { JhiEventManager } from 'ng-jhipster';

import { ImputationSqliTestModule } from '../../../test.module';
import { ImputationTypeDeleteDialogComponent } from 'app/entities/imputation-type/imputation-type-delete-dialog.component';
import { ImputationTypeService } from 'app/entities/imputation-type/imputation-type.service';

describe('Component Tests', () => {
    describe('ImputationType Management Delete Component', () => {
        let comp: ImputationTypeDeleteDialogComponent;
        let fixture: ComponentFixture<ImputationTypeDeleteDialogComponent>;
        let service: ImputationTypeService;
        let mockEventManager: any;
        let mockActiveModal: any;

        beforeEach(() => {
            TestBed.configureTestingModule({
                imports: [ImputationSqliTestModule],
                declarations: [ImputationTypeDeleteDialogComponent]
            })
                .overrideTemplate(ImputationTypeDeleteDialogComponent, '')
                .compileComponents();
            fixture = TestBed.createComponent(ImputationTypeDeleteDialogComponent);
            comp = fixture.componentInstance;
            service = fixture.debugElement.injector.get(ImputationTypeService);
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
