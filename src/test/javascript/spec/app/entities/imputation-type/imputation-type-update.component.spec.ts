/* tslint:disable max-line-length */
import { ComponentFixture, TestBed, fakeAsync, tick } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { Observable, of } from 'rxjs';

import { ImputationSqliTestModule } from '../../../test.module';
import { ImputationTypeUpdateComponent } from 'app/entities/imputation-type/imputation-type-update.component';
import { ImputationTypeService } from 'app/entities/imputation-type/imputation-type.service';
import { ImputationType } from 'app/shared/model/imputation-type.model';

describe('Component Tests', () => {
    describe('ImputationType Management Update Component', () => {
        let comp: ImputationTypeUpdateComponent;
        let fixture: ComponentFixture<ImputationTypeUpdateComponent>;
        let service: ImputationTypeService;

        beforeEach(() => {
            TestBed.configureTestingModule({
                imports: [ImputationSqliTestModule],
                declarations: [ImputationTypeUpdateComponent]
            })
                .overrideTemplate(ImputationTypeUpdateComponent, '')
                .compileComponents();

            fixture = TestBed.createComponent(ImputationTypeUpdateComponent);
            comp = fixture.componentInstance;
            service = fixture.debugElement.injector.get(ImputationTypeService);
        });

        describe('save', () => {
            it('Should call update service on save for existing entity', fakeAsync(() => {
                // GIVEN
                const entity = new ImputationType(123);
                spyOn(service, 'update').and.returnValue(of(new HttpResponse({ body: entity })));
                comp.imputationType = entity;
                // WHEN
                comp.save();
                tick(); // simulate async

                // THEN
                expect(service.update).toHaveBeenCalledWith(entity);
                expect(comp.isSaving).toEqual(false);
            }));

            it('Should call create service on save for new entity', fakeAsync(() => {
                // GIVEN
                const entity = new ImputationType();
                spyOn(service, 'create').and.returnValue(of(new HttpResponse({ body: entity })));
                comp.imputationType = entity;
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
