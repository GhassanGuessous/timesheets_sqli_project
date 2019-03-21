/* tslint:disable max-line-length */
import { ComponentFixture, TestBed, fakeAsync, tick } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { Observable, of } from 'rxjs';

import { ImputationSqliTestModule } from '../../../test.module';
import { CollaboratorUpdateComponent } from 'app/entities/collaborator/collaborator-update.component';
import { CollaboratorService } from 'app/entities/collaborator/collaborator.service';
import { Collaborator } from 'app/shared/model/collaborator.model';

describe('Component Tests', () => {
    describe('Collaborator Management Update Component', () => {
        let comp: CollaboratorUpdateComponent;
        let fixture: ComponentFixture<CollaboratorUpdateComponent>;
        let service: CollaboratorService;

        beforeEach(() => {
            TestBed.configureTestingModule({
                imports: [ImputationSqliTestModule],
                declarations: [CollaboratorUpdateComponent]
            })
                .overrideTemplate(CollaboratorUpdateComponent, '')
                .compileComponents();

            fixture = TestBed.createComponent(CollaboratorUpdateComponent);
            comp = fixture.componentInstance;
            service = fixture.debugElement.injector.get(CollaboratorService);
        });

        describe('save', () => {
            it('Should call update service on save for existing entity', fakeAsync(() => {
                // GIVEN
                const entity = new Collaborator(123);
                spyOn(service, 'update').and.returnValue(of(new HttpResponse({ body: entity })));
                comp.collaborator = entity;
                // WHEN
                comp.save();
                tick(); // simulate async

                // THEN
                expect(service.update).toHaveBeenCalledWith(entity);
                expect(comp.isSaving).toEqual(false);
            }));

            it('Should call create service on save for new entity', fakeAsync(() => {
                // GIVEN
                const entity = new Collaborator();
                spyOn(service, 'create').and.returnValue(of(new HttpResponse({ body: entity })));
                comp.collaborator = entity;
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
