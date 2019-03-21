/* tslint:disable max-line-length */
import { ComponentFixture, TestBed, inject, fakeAsync, tick } from '@angular/core/testing';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';
import { Observable, of } from 'rxjs';
import { JhiEventManager } from 'ng-jhipster';

import { ImputationSqliTestModule } from '../../../test.module';
import { CollaboratorDeleteDialogComponent } from 'app/entities/collaborator/collaborator-delete-dialog.component';
import { CollaboratorService } from 'app/entities/collaborator/collaborator.service';

describe('Component Tests', () => {
    describe('Collaborator Management Delete Component', () => {
        let comp: CollaboratorDeleteDialogComponent;
        let fixture: ComponentFixture<CollaboratorDeleteDialogComponent>;
        let service: CollaboratorService;
        let mockEventManager: any;
        let mockActiveModal: any;

        beforeEach(() => {
            TestBed.configureTestingModule({
                imports: [ImputationSqliTestModule],
                declarations: [CollaboratorDeleteDialogComponent]
            })
                .overrideTemplate(CollaboratorDeleteDialogComponent, '')
                .compileComponents();
            fixture = TestBed.createComponent(CollaboratorDeleteDialogComponent);
            comp = fixture.componentInstance;
            service = fixture.debugElement.injector.get(CollaboratorService);
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
