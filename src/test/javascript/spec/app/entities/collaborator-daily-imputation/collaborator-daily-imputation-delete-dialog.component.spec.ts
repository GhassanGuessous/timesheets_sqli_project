/* tslint:disable max-line-length */
import { ComponentFixture, TestBed, inject, fakeAsync, tick } from '@angular/core/testing';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';
import { Observable, of } from 'rxjs';
import { JhiEventManager } from 'ng-jhipster';

import { ImputationSqliTestModule } from '../../../test.module';
import { CollaboratorDailyImputationDeleteDialogComponent } from 'app/entities/collaborator-daily-imputation/collaborator-daily-imputation-delete-dialog.component';
import { CollaboratorDailyImputationService } from 'app/entities/collaborator-daily-imputation/collaborator-daily-imputation.service';

describe('Component Tests', () => {
    describe('CollaboratorDailyImputation Management Delete Component', () => {
        let comp: CollaboratorDailyImputationDeleteDialogComponent;
        let fixture: ComponentFixture<CollaboratorDailyImputationDeleteDialogComponent>;
        let service: CollaboratorDailyImputationService;
        let mockEventManager: any;
        let mockActiveModal: any;

        beforeEach(() => {
            TestBed.configureTestingModule({
                imports: [ImputationSqliTestModule],
                declarations: [CollaboratorDailyImputationDeleteDialogComponent]
            })
                .overrideTemplate(CollaboratorDailyImputationDeleteDialogComponent, '')
                .compileComponents();
            fixture = TestBed.createComponent(CollaboratorDailyImputationDeleteDialogComponent);
            comp = fixture.componentInstance;
            service = fixture.debugElement.injector.get(CollaboratorDailyImputationService);
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
