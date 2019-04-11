import { NgModule, CUSTOM_ELEMENTS_SCHEMA } from '@angular/core';
import { RouterModule } from '@angular/router';
import { TimesheetTbpComponent } from './timesheet-tbp/timesheet-tbp.component';
import { TimesheetPpmcComponent } from './timesheet-ppmc/timesheet-ppmc.component';

@NgModule({
    imports: [
        RouterModule.forChild([
            {
                path: 'delivery-coordinator',
                loadChildren: './delivery-coordinator/delivery-coordinator.module#ImputationSqliDeliveryCoordinatorModule'
            },
            {
                path: 'administrator',
                loadChildren: './administrator/administrator.module#ImputationSqliAdministratorModule'
            },
            {
                path: 'team',
                loadChildren: './team/team.module#ImputationSqliTeamModule'
            },
            {
                path: 'project',
                loadChildren: './project/project.module#ImputationSqliProjectModule'
            },
            {
                path: 'project-type',
                loadChildren: './project-type/project-type.module#ImputationSqliProjectTypeModule'
            },
            {
                path: 'collaborator',
                loadChildren: './collaborator/collaborator.module#ImputationSqliCollaboratorModule'
            },
            {
                path: 'activity',
                loadChildren: './activity/activity.module#ImputationSqliActivityModule'
            },
            {
                path: 'correspondence',
                loadChildren: './correspondence/correspondence.module#ImputationSqliCorrespondenceModule'
            },
            {
                path: 'imputation',
                loadChildren: './imputation/imputation.module#ImputationSqliImputationModule'
            },
            {
                path: 'imputation-type',
                loadChildren: './imputation-type/imputation-type.module#ImputationSqliImputationTypeModule'
            },
            {
                path: 'collaborator-monthly-imputation',
                loadChildren:
                    './collaborator-monthly-imputation/collaborator-monthly-imputation.module#ImputationSqliCollaboratorMonthlyImputationModule'
            },
            {
                path: 'collaborator-daily-imputation',
                loadChildren:
                    './collaborator-daily-imputation/collaborator-daily-imputation.module#ImputationSqliCollaboratorDailyImputationModule'
            },
            /* jhipster-needle-add-entity-route - JHipster will add entity modules routes here */
            {
                path: 'timesheet-app',
                loadChildren: './timesheet-app/timesheet-app.module#ImputationSqliTimesheetAppModule'
            },
            {
                path: 'timesheet-tbp',
                loadChildren: './timesheet-tbp/timesheet-tbp.module#ImputationSqliTimesheetTbpModule'
            },
            {
                path: 'timesheet-ppmc',
                loadChildren: './timesheet-ppmc/timesheet-ppmc.module#ImputationSqliTimesheetPpmcModule'
            }
        ])
    ],
    declarations: [],
    entryComponents: [],
    providers: [],
    schemas: [CUSTOM_ELEMENTS_SCHEMA]
})
export class ImputationSqliEntityModule {}
