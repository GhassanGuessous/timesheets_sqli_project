/* tslint:disable max-line-length */
import { ComponentFixture, TestBed, fakeAsync, tick } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { Observable, of } from 'rxjs';

import { ImputationSqliTestModule } from '../../../test.module';
import { ImputationUpdateComponent } from 'app/entities/imputation/imputation-update.component';
import { ImputationService } from 'app/entities/imputation/imputation.service';
import { Imputation } from 'app/shared/model/imputation.model';

describe('Component Tests', () => {
    describe('Imputation Management Update Component', () => {
        let comp: ImputationUpdateComponent;
        let fixture: ComponentFixture<ImputationUpdateComponent>;
        let service: ImputationService;

        beforeEach(() => {
            TestBed.configureTestingModule({
                imports: [ImputationSqliTestModule],
                declarations: [ImputationUpdateComponent]
            })
                .overrideTemplate(ImputationUpdateComponent, '')
                .compileComponents();

            fixture = TestBed.createComponent(ImputationUpdateComponent);
            comp = fixture.componentInstance;
            service = fixture.debugElement.injector.get(ImputationService);
        });

        describe('save', () => {
            it('Should call update service on save for existing entity', fakeAsync(() => {
                // GIVEN
                const entity = new Imputation(123);
                spyOn(service, 'update').and.returnValue(of(new HttpResponse({ body: entity })));
                comp.imputation = entity;
                // WHEN
                comp.save();
                tick(); // simulate async

                // THEN
                expect(service.update).toHaveBeenCalledWith(entity);
                expect(comp.isSaving).toEqual(false);
            }));

            it('Should call create service on save for new entity', fakeAsync(() => {
                // GIVEN
                const entity = new Imputation();
                spyOn(service, 'create').and.returnValue(of(new HttpResponse({ body: entity })));
                comp.imputation = entity;
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
