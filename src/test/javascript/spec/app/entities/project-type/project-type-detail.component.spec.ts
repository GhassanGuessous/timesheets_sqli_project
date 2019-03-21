/* tslint:disable max-line-length */
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';
import { of } from 'rxjs';

import { ImputationSqliTestModule } from '../../../test.module';
import { ProjectTypeDetailComponent } from 'app/entities/project-type/project-type-detail.component';
import { ProjectType } from 'app/shared/model/project-type.model';

describe('Component Tests', () => {
    describe('ProjectType Management Detail Component', () => {
        let comp: ProjectTypeDetailComponent;
        let fixture: ComponentFixture<ProjectTypeDetailComponent>;
        const route = ({ data: of({ projectType: new ProjectType(123) }) } as any) as ActivatedRoute;

        beforeEach(() => {
            TestBed.configureTestingModule({
                imports: [ImputationSqliTestModule],
                declarations: [ProjectTypeDetailComponent],
                providers: [{ provide: ActivatedRoute, useValue: route }]
            })
                .overrideTemplate(ProjectTypeDetailComponent, '')
                .compileComponents();
            fixture = TestBed.createComponent(ProjectTypeDetailComponent);
            comp = fixture.componentInstance;
        });

        describe('OnInit', () => {
            it('Should call load all on init', () => {
                // GIVEN

                // WHEN
                comp.ngOnInit();

                // THEN
                expect(comp.projectType).toEqual(jasmine.objectContaining({ id: 123 }));
            });
        });
    });
});
