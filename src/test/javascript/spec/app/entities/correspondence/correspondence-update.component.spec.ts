/* tslint:disable max-line-length */
import { ComponentFixture, TestBed, fakeAsync, tick } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { Observable, of } from 'rxjs';

import { ImputationSqliTestModule } from '../../../test.module';
import { CorrespondenceUpdateComponent } from 'app/entities/correspondence/correspondence-update.component';
import { CorrespondenceService } from 'app/entities/correspondence/correspondence.service';
import { Correspondence } from 'app/shared/model/correspondence.model';

describe('Component Tests', () => {
    describe('Correspondence Management Update Component', () => {
        let comp: CorrespondenceUpdateComponent;
        let fixture: ComponentFixture<CorrespondenceUpdateComponent>;
        let service: CorrespondenceService;

        beforeEach(() => {
            TestBed.configureTestingModule({
                imports: [ImputationSqliTestModule],
                declarations: [CorrespondenceUpdateComponent]
            })
                .overrideTemplate(CorrespondenceUpdateComponent, '')
                .compileComponents();

            fixture = TestBed.createComponent(CorrespondenceUpdateComponent);
            comp = fixture.componentInstance;
            service = fixture.debugElement.injector.get(CorrespondenceService);
        });

        describe('save', () => {
            it('Should call update service on save for existing entity', fakeAsync(() => {
                // GIVEN
                const entity = new Correspondence(123);
                spyOn(service, 'update').and.returnValue(of(new HttpResponse({ body: entity })));
                comp.correspondence = entity;
                // WHEN
                comp.save();
                tick(); // simulate async

                // THEN
                expect(service.update).toHaveBeenCalledWith(entity);
                expect(comp.isSaving).toEqual(false);
            }));

            it('Should call create service on save for new entity', fakeAsync(() => {
                // GIVEN
                const entity = new Correspondence();
                spyOn(service, 'create').and.returnValue(of(new HttpResponse({ body: entity })));
                comp.correspondence = entity;
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
