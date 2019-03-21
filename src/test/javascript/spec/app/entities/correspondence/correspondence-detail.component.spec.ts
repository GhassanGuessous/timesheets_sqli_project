/* tslint:disable max-line-length */
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';
import { of } from 'rxjs';

import { ImputationSqliTestModule } from '../../../test.module';
import { CorrespondenceDetailComponent } from 'app/entities/correspondence/correspondence-detail.component';
import { Correspondence } from 'app/shared/model/correspondence.model';

describe('Component Tests', () => {
    describe('Correspondence Management Detail Component', () => {
        let comp: CorrespondenceDetailComponent;
        let fixture: ComponentFixture<CorrespondenceDetailComponent>;
        const route = ({ data: of({ correspondence: new Correspondence(123) }) } as any) as ActivatedRoute;

        beforeEach(() => {
            TestBed.configureTestingModule({
                imports: [ImputationSqliTestModule],
                declarations: [CorrespondenceDetailComponent],
                providers: [{ provide: ActivatedRoute, useValue: route }]
            })
                .overrideTemplate(CorrespondenceDetailComponent, '')
                .compileComponents();
            fixture = TestBed.createComponent(CorrespondenceDetailComponent);
            comp = fixture.componentInstance;
        });

        describe('OnInit', () => {
            it('Should call load all on init', () => {
                // GIVEN

                // WHEN
                comp.ngOnInit();

                // THEN
                expect(comp.correspondence).toEqual(jasmine.objectContaining({ id: 123 }));
            });
        });
    });
});
