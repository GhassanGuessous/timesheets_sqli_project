/* tslint:disable max-line-length */
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';
import { of } from 'rxjs';

import { ImputationSqliTestModule } from '../../../test.module';
import { ImputationTypeDetailComponent } from 'app/entities/imputation-type/imputation-type-detail.component';
import { ImputationType } from 'app/shared/model/imputation-type.model';

describe('Component Tests', () => {
    describe('ImputationType Management Detail Component', () => {
        let comp: ImputationTypeDetailComponent;
        let fixture: ComponentFixture<ImputationTypeDetailComponent>;
        const route = ({ data: of({ imputationType: new ImputationType(123) }) } as any) as ActivatedRoute;

        beforeEach(() => {
            TestBed.configureTestingModule({
                imports: [ImputationSqliTestModule],
                declarations: [ImputationTypeDetailComponent],
                providers: [{ provide: ActivatedRoute, useValue: route }]
            })
                .overrideTemplate(ImputationTypeDetailComponent, '')
                .compileComponents();
            fixture = TestBed.createComponent(ImputationTypeDetailComponent);
            comp = fixture.componentInstance;
        });

        describe('OnInit', () => {
            it('Should call load all on init', () => {
                // GIVEN

                // WHEN
                comp.ngOnInit();

                // THEN
                expect(comp.imputationType).toEqual(jasmine.objectContaining({ id: 123 }));
            });
        });
    });
});
