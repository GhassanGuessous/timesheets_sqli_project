/* tslint:disable max-line-length */
import { ComponentFixture, TestBed, inject, fakeAsync, tick } from '@angular/core/testing';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';
import { Observable, of } from 'rxjs';
import { JhiEventManager } from 'ng-jhipster';

import { ImputationSqliTestModule } from '../../../test.module';
import { CorrespondenceDeleteDialogComponent } from 'app/entities/correspondence/correspondence-delete-dialog.component';
import { CorrespondenceService } from 'app/entities/correspondence/correspondence.service';

describe('Component Tests', () => {
    describe('Correspondence Management Delete Component', () => {
        let comp: CorrespondenceDeleteDialogComponent;
        let fixture: ComponentFixture<CorrespondenceDeleteDialogComponent>;
        let service: CorrespondenceService;
        let mockEventManager: any;
        let mockActiveModal: any;

        beforeEach(() => {
            TestBed.configureTestingModule({
                imports: [ImputationSqliTestModule],
                declarations: [CorrespondenceDeleteDialogComponent]
            })
                .overrideTemplate(CorrespondenceDeleteDialogComponent, '')
                .compileComponents();
            fixture = TestBed.createComponent(CorrespondenceDeleteDialogComponent);
            comp = fixture.componentInstance;
            service = fixture.debugElement.injector.get(CorrespondenceService);
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
