/* tslint:disable max-line-length */
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';
import { of } from 'rxjs';

import { ImputationSqliTestModule } from '../../../test.module';
import { ImputationDetailComponent } from 'app/entities/imputation/imputation-detail.component';
import { Imputation } from 'app/shared/model/imputation.model';

describe('Component Tests', () => {
    describe('Imputation Management Detail Component', () => {
        let comp: ImputationDetailComponent;
        let fixture: ComponentFixture<ImputationDetailComponent>;
        const route = ({ data: of({ imputation: new Imputation(123) }) } as any) as ActivatedRoute;

        beforeEach(() => {
            TestBed.configureTestingModule({
                imports: [ImputationSqliTestModule],
                declarations: [ImputationDetailComponent],
                providers: [{ provide: ActivatedRoute, useValue: route }]
            })
                .overrideTemplate(ImputationDetailComponent, '')
                .compileComponents();
            fixture = TestBed.createComponent(ImputationDetailComponent);
            comp = fixture.componentInstance;
        });

        describe('OnInit', () => {
            it('Should call load all on init', () => {
                // GIVEN

                // WHEN
                comp.ngOnInit();

                // THEN
                expect(comp.imputation).toEqual(jasmine.objectContaining({ id: 123 }));
            });
        });
    });
});
