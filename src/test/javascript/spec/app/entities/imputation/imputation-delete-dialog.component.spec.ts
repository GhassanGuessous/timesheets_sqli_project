/* tslint:disable max-line-length */
import { ComponentFixture, TestBed, inject, fakeAsync, tick } from '@angular/core/testing';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';
import { Observable, of } from 'rxjs';
import { JhiEventManager } from 'ng-jhipster';

import { ImputationSqliTestModule } from '../../../test.module';
import { ImputationDeleteDialogComponent } from 'app/entities/imputation/imputation-delete-dialog.component';
import { ImputationService } from 'app/entities/imputation/imputation.service';

describe('Component Tests', () => {
    describe('Imputation Management Delete Component', () => {
        let comp: ImputationDeleteDialogComponent;
        let fixture: ComponentFixture<ImputationDeleteDialogComponent>;
        let service: ImputationService;
        let mockEventManager: any;
        let mockActiveModal: any;

        beforeEach(() => {
            TestBed.configureTestingModule({
                imports: [ImputationSqliTestModule],
                declarations: [ImputationDeleteDialogComponent]
            })
                .overrideTemplate(ImputationDeleteDialogComponent, '')
                .compileComponents();
            fixture = TestBed.createComponent(ImputationDeleteDialogComponent);
            comp = fixture.componentInstance;
            service = fixture.debugElement.injector.get(ImputationService);
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
