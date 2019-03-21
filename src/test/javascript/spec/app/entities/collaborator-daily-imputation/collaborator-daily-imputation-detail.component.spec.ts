/* tslint:disable max-line-length */
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';
import { of } from 'rxjs';

import { ImputationSqliTestModule } from '../../../test.module';
import { CollaboratorDailyImputationDetailComponent } from 'app/entities/collaborator-daily-imputation/collaborator-daily-imputation-detail.component';
import { CollaboratorDailyImputation } from 'app/shared/model/collaborator-daily-imputation.model';

describe('Component Tests', () => {
    describe('CollaboratorDailyImputation Management Detail Component', () => {
        let comp: CollaboratorDailyImputationDetailComponent;
        let fixture: ComponentFixture<CollaboratorDailyImputationDetailComponent>;
        const route = ({ data: of({ collaboratorDailyImputation: new CollaboratorDailyImputation(123) }) } as any) as ActivatedRoute;

        beforeEach(() => {
            TestBed.configureTestingModule({
                imports: [ImputationSqliTestModule],
                declarations: [CollaboratorDailyImputationDetailComponent],
                providers: [{ provide: ActivatedRoute, useValue: route }]
            })
                .overrideTemplate(CollaboratorDailyImputationDetailComponent, '')
                .compileComponents();
            fixture = TestBed.createComponent(CollaboratorDailyImputationDetailComponent);
            comp = fixture.componentInstance;
        });

        describe('OnInit', () => {
            it('Should call load all on init', () => {
                // GIVEN

                // WHEN
                comp.ngOnInit();

                // THEN
                expect(comp.collaboratorDailyImputation).toEqual(jasmine.objectContaining({ id: 123 }));
            });
        });
    });
});
