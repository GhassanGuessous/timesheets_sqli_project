/* tslint:disable max-line-length */
import { ComponentFixture, TestBed, fakeAsync, tick } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { Observable, of } from 'rxjs';

import { ImputationSqliTestModule } from '../../../test.module';
import { CollaboratorMonthlyImputationUpdateComponent } from 'app/entities/collaborator-monthly-imputation/collaborator-monthly-imputation-update.component';
import { CollaboratorMonthlyImputationService } from 'app/entities/collaborator-monthly-imputation/collaborator-monthly-imputation.service';
import { CollaboratorMonthlyImputation } from 'app/shared/model/collaborator-monthly-imputation.model';

describe('Component Tests', () => {
    describe('CollaboratorMonthlyImputation Management Update Component', () => {
        let comp: CollaboratorMonthlyImputationUpdateComponent;
        let fixture: ComponentFixture<CollaboratorMonthlyImputationUpdateComponent>;
        let service: CollaboratorMonthlyImputationService;

        beforeEach(() => {
            TestBed.configureTestingModule({
                imports: [ImputationSqliTestModule],
                declarations: [CollaboratorMonthlyImputationUpdateComponent]
            })
                .overrideTemplate(CollaboratorMonthlyImputationUpdateComponent, '')
                .compileComponents();

            fixture = TestBed.createComponent(CollaboratorMonthlyImputationUpdateComponent);
            comp = fixture.componentInstance;
            service = fixture.debugElement.injector.get(CollaboratorMonthlyImputationService);
        });

        describe('save', () => {
            it('Should call update service on save for existing entity', fakeAsync(() => {
                // GIVEN
                const entity = new CollaboratorMonthlyImputation(123);
                spyOn(service, 'update').and.returnValue(of(new HttpResponse({ body: entity })));
                comp.collaboratorMonthlyImputation = entity;
                // WHEN
                comp.save();
                tick(); // simulate async

                // THEN
                expect(service.update).toHaveBeenCalledWith(entity);
                expect(comp.isSaving).toEqual(false);
            }));

            it('Should call create service on save for new entity', fakeAsync(() => {
                // GIVEN
                const entity = new CollaboratorMonthlyImputation();
                spyOn(service, 'create').and.returnValue(of(new HttpResponse({ body: entity })));
                comp.collaboratorMonthlyImputation = entity;
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
