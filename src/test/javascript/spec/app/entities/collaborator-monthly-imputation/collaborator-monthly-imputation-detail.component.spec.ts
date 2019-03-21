/* tslint:disable max-line-length */
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';
import { of } from 'rxjs';

import { ImputationSqliTestModule } from '../../../test.module';
import { CollaboratorMonthlyImputationDetailComponent } from 'app/entities/collaborator-monthly-imputation/collaborator-monthly-imputation-detail.component';
import { CollaboratorMonthlyImputation } from 'app/shared/model/collaborator-monthly-imputation.model';

describe('Component Tests', () => {
    describe('CollaboratorMonthlyImputation Management Detail Component', () => {
        let comp: CollaboratorMonthlyImputationDetailComponent;
        let fixture: ComponentFixture<CollaboratorMonthlyImputationDetailComponent>;
        const route = ({ data: of({ collaboratorMonthlyImputation: new CollaboratorMonthlyImputation(123) }) } as any) as ActivatedRoute;

        beforeEach(() => {
            TestBed.configureTestingModule({
                imports: [ImputationSqliTestModule],
                declarations: [CollaboratorMonthlyImputationDetailComponent],
                providers: [{ provide: ActivatedRoute, useValue: route }]
            })
                .overrideTemplate(CollaboratorMonthlyImputationDetailComponent, '')
                .compileComponents();
            fixture = TestBed.createComponent(CollaboratorMonthlyImputationDetailComponent);
            comp = fixture.componentInstance;
        });

        describe('OnInit', () => {
            it('Should call load all on init', () => {
                // GIVEN

                // WHEN
                comp.ngOnInit();

                // THEN
                expect(comp.collaboratorMonthlyImputation).toEqual(jasmine.objectContaining({ id: 123 }));
            });
        });
    });
});
