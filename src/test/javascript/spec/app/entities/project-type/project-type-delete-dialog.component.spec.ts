/* tslint:disable max-line-length */
import { ComponentFixture, TestBed, inject, fakeAsync, tick } from '@angular/core/testing';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';
import { Observable, of } from 'rxjs';
import { JhiEventManager } from 'ng-jhipster';

import { ImputationSqliTestModule } from '../../../test.module';
import { ProjectTypeDeleteDialogComponent } from 'app/entities/project-type/project-type-delete-dialog.component';
import { ProjectTypeService } from 'app/entities/project-type/project-type.service';

describe('Component Tests', () => {
    describe('ProjectType Management Delete Component', () => {
        let comp: ProjectTypeDeleteDialogComponent;
        let fixture: ComponentFixture<ProjectTypeDeleteDialogComponent>;
        let service: ProjectTypeService;
        let mockEventManager: any;
        let mockActiveModal: any;

        beforeEach(() => {
            TestBed.configureTestingModule({
                imports: [ImputationSqliTestModule],
                declarations: [ProjectTypeDeleteDialogComponent]
            })
                .overrideTemplate(ProjectTypeDeleteDialogComponent, '')
                .compileComponents();
            fixture = TestBed.createComponent(ProjectTypeDeleteDialogComponent);
            comp = fixture.componentInstance;
            service = fixture.debugElement.injector.get(ProjectTypeService);
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
