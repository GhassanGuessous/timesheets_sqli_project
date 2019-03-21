/* tslint:disable max-line-length */
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';
import { of } from 'rxjs';

import { ImputationSqliTestModule } from '../../../test.module';
import { CollaboratorDetailComponent } from 'app/entities/collaborator/collaborator-detail.component';
import { Collaborator } from 'app/shared/model/collaborator.model';

describe('Component Tests', () => {
    describe('Collaborator Management Detail Component', () => {
        let comp: CollaboratorDetailComponent;
        let fixture: ComponentFixture<CollaboratorDetailComponent>;
        const route = ({ data: of({ collaborator: new Collaborator(123) }) } as any) as ActivatedRoute;

        beforeEach(() => {
            TestBed.configureTestingModule({
                imports: [ImputationSqliTestModule],
                declarations: [CollaboratorDetailComponent],
                providers: [{ provide: ActivatedRoute, useValue: route }]
            })
                .overrideTemplate(CollaboratorDetailComponent, '')
                .compileComponents();
            fixture = TestBed.createComponent(CollaboratorDetailComponent);
            comp = fixture.componentInstance;
        });

        describe('OnInit', () => {
            it('Should call load all on init', () => {
                // GIVEN

                // WHEN
                comp.ngOnInit();

                // THEN
                expect(comp.collaborator).toEqual(jasmine.objectContaining({ id: 123 }));
            });
        });
    });
});
