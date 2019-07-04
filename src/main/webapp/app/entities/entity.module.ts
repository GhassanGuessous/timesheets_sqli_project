import { NgModule, CUSTOM_ELEMENTS_SCHEMA } from '@angular/core';
import { RouterModule } from '@angular/router';

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
            },
            {
                path: 'timesheet-jira',
                loadChildren: './timesheet-jira/timesheet-jira.module#ImputationSqliTimesheetJiraModule'
            },
            {
                path: 'comparator-app-tbp',
                loadChildren: './comparator-app-tbp/comparator-app-tbp.module#ComparatorAPPTBPModule'
            },
            {
                path: 'comparator-app-ppmc',
                loadChildren: './comparator-app-ppmc/comparator-app-ppmc.module#ComparatorAppPpmcModule'
            },
            {
                path: 'advanced-comparator-app-tbp',
                loadChildren: './comparator-app-tbp-advanced/comparator-app-tbp-advanced.module#ComparatorAppTbpAdvancedModule'
            },
            {
                path: 'advanced-comparator-app-ppmc',
                loadChildren: './comparator-app-ppmc-advanced/comparator-app-ppmc-advanced.module#ComparatorAppPpmcAdvancedModule'
            },
            {
                path: 'statistics',
                loadChildren: './gap-per-team-statistics/gap-per-team-statistics.module#GapPerTeamStatisticsModule'
            },
            {
                path: 'gap-variation-statistics',
                loadChildren: './gap-variation-statistics/gap-variation-statistics.module#GapVariationStatisticsModule'
            },
            {
                path: 'ppmc-projects-workloged-statistics',
                loadChildren:
                    './ppmc-projects-workloged-statistics/ppmc-projects-workloged-statistics.module#PpmcProjectsWorklogedStatisticsModule'
            },
            {
                path: 'issue-type-statistics',
                loadChildren: './issue-type-statistics/issue-type-statistics.module#IssueTypeStatisticsModule'
            }
        ])
    ],
    declarations: [],
    entryComponents: [],
    providers: [],
    schemas: [CUSTOM_ELEMENTS_SCHEMA]
})
export class ImputationSqliEntityModule {}
