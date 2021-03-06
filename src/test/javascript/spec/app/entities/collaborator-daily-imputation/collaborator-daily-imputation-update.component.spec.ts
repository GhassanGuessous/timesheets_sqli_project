/* tslint:disable max-line-length */
import { ComponentFixture, TestBed, fakeAsync, tick } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { Observable, of } from 'rxjs';

import { ImputationSqliTestModule } from '../../../test.module';
import { CollaboratorDailyImputationUpdateComponent } from 'app/entities/collaborator-daily-imputation/collaborator-daily-imputation-update.component';
import { CollaboratorDailyImputationService } from 'app/entities/collaborator-daily-imputation/collaborator-daily-imputation.service';
import { CollaboratorDailyImputation } from 'app/shared/model/collaborator-daily-imputation.model';

describe('Component Tests', () => {
    describe('CollaboratorDailyImputation Management Update Component', () => {
        let comp: CollaboratorDailyImputationUpdateComponent;
        let fixture: ComponentFixture<CollaboratorDailyImputationUpdateComponent>;
        let service: CollaboratorDailyImputationService;

        beforeEach(() => {
            TestBed.configureTestingModule({
                imports: [ImputationSqliTestModule],
                declarations: [CollaboratorDailyImputationUpdateComponent]
            })
                .overrideTemplate(CollaboratorDailyImputationUpdateComponent, '')
                .compileComponents();

            fixture = TestBed.createComponent(CollaboratorDailyImputationUpdateComponent);
            comp = fixture.componentInstance;
            service = fixture.debugElement.injector.get(CollaboratorDailyImputationService);
        });

        describe('save', () => {
            it('Should call update service on save for existing entity', fakeAsync(() => {
                // GIVEN
                const entity = new CollaboratorDailyImputation(123);
                spyOn(service, 'update').and.returnValue(of(new HttpResponse({ body: entity })));
                comp.collaboratorDailyImputation = entity;
                // WHEN
                comp.save();
                tick(); // simulate async

                // THEN
                expect(service.update).toHaveBeenCalledWith(entity);
                expect(comp.isSaving).toEqual(false);
            }));

            it('Should call create service on save for new entity', fakeAsync(() => {
                // GIVEN
                const entity = new CollaboratorDailyImputation();
                spyOn(service, 'create').and.returnValue(of(new HttpResponse({ body: entity })));
                comp.collaboratorDailyImputation = entity;
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
