import { Component, ElementRef, NgZone, OnDestroy, OnInit, ViewChild } from '@angular/core';
import * as am4charts from '@amcharts/amcharts4/charts';
import { ITbpRequestBody, TbpRequestBody } from 'app/shared/model/tbp-request-body';
import { ITeam } from 'app/shared/model/team.model';
import { AccountService } from 'app/core';
import { TeamService } from 'app/entities/team';
import { AuthJiraModalService } from 'app/core/authJira/auth-jira-modal.service';
import { DatePipe } from '@angular/common';
import * as am4core from '@amcharts/amcharts4/core';
import { IssueTypeStatisticsService } from 'app/entities/issue-type-statistics/issue-type-statistics.service';

@Component({
    selector: 'jhi-issue-type-statistics',
    templateUrl: './issue-type-statistics.component.html',
    styles: []
})
export class IssueTypeStatisticsComponent implements OnInit, OnDestroy {
    @ViewChild('issueTypeChart') issueTypeChart: ElementRef;
    @ViewChild('frequancyChart') frequancyChart: ElementRef;

    private chart: am4charts.XYChart;
    private issueTypeWorklog: any;
    requestBody: ITbpRequestBody = new TbpRequestBody();
    private currentAccount: any;
    private allTeams: ITeam[];
    private myTeam: ITeam;

    constructor(
        protected accountService: AccountService,
        protected teamService: TeamService,
        protected issueTypeStatisticsService: IssueTypeStatisticsService,
        private authJiraModalService: AuthJiraModalService,
        private zone: NgZone,
        private datePipe: DatePipe
    ) {}

    ngOnInit() {
        this.accountService.identity().then(account => {
            this.currentAccount = account;
            if (this.isAdmin()) {
                this.loadAll();
            } else {
                this.loadDelcoTeam(account.id);
            }
        });
        this.initialize();
    }

    // ngAfterViewInit() {
    //     setTimeout(() => {
    //         this.getStatistics();
    //     }, 1000);
    // }

    private initialize() {
        this.today();
        this.previousMonth();
    }

    private previousMonth() {
        const dateFormat = 'yyyy-MM-dd';
        let fromDate: Date = new Date();

        if (fromDate.getMonth() === 0) {
            fromDate = new Date(fromDate.getFullYear() - 1, 11, fromDate.getDate());
        } else {
            fromDate = new Date(fromDate.getFullYear(), fromDate.getMonth() - 1, fromDate.getDate());
        }

        this.requestBody.startDate = this.datePipe.transform(fromDate, dateFormat);
    }

    private today() {
        const dateFormat = 'yyyy-MM-dd';
        // Today + 1 day - needed if the current day must be included
        const today: Date = new Date();
        today.setDate(today.getDate() + 1);
        const date = new Date(today.getFullYear(), today.getMonth(), today.getDate());
        this.requestBody.endDate = this.datePipe.transform(date, dateFormat);
    }

    private loadAll() {
        this.teamService.findAllTeamsWithoutPagination().subscribe(res => {
            this.allTeams = res.body;
        });
    }

    private loadDelcoTeam(id: bigint) {
        this.teamService.findByDelco(id).subscribe(data => {
            this.myTeam = data.body;
            this.requestBody.idTbp = this.myTeam.idTbp;
        });
    }

    private isAdmin() {
        return this.currentAccount.authorities.includes('ROLE_ADMIN');
    }

    getStatistics() {
        if (localStorage.getItem('isJiraAuthenticated') === 'false') {
            this.authenticateThenGetStatistics();
        } else {
            this.getStatisticsWhenIsAlreadyAuthenticated();
        }
    }

    private authenticateThenGetStatistics() {
        this.requestBody.requestType = 'JIRA_ISSUE_TYPE_STATISTICS';
        this.authJiraModalService.open(this.requestBody).then(
            result => {
                this.issueTypeWorklog = result;
                console.log(result);
                this.zone.runOutsideAngular(() => {
                    this.createCharts(this.issueTypeWorklog);
                });
            },
            reason => {
                console.log(reason);
            }
        );
    }

    private getStatisticsWhenIsAlreadyAuthenticated() {
        this.requestBody.username = localStorage.getItem('jiraUsername');
        this.requestBody.password = localStorage.getItem('jiraPassword');
        this.issueTypeWorklog = this.issueTypeStatisticsService.getIssueTypeStatistics(this.requestBody).subscribe(data => {
            this.issueTypeWorklog = data.body;
            console.log(data.body);
            this.zone.runOutsideAngular(() => {
                this.createCharts(this.issueTypeWorklog);
            });
        });
    }

    private createWorklogChart(data) {
        const htmlElement = this.issueTypeChart.nativeElement;
        const chart = am4core.create(htmlElement, am4charts.PieChart);
        chart.data = data;

        chart.legend = new am4charts.Legend();
        chart.hiddenState.properties.opacity = 0;

        const series = chart.series.push(new am4charts.PieSeries());
        series.dataFields.value = 'totalMinutes';
        series.dataFields.category = 'issueType';
        series.labels.template.text = '{issueType}s: {timeSpent}';
        series.labels.template.tooltipText = '{timeSpent}';
        series.slices.template.tooltipText = '{category}: {timeSpent}';
        series.legendSettings.labelText = '[bold {color}]{issueType}[/]';
    }
    private createFrequancyChart(data) {
        const htmlElement = this.frequancyChart.nativeElement;
        const chart = am4core.create(htmlElement, am4charts.PieChart);
        chart.data = data;

        chart.legend = new am4charts.Legend();
        chart.hiddenState.properties.opacity = 0;
        chart.radius = am4core.percent(70);
        chart.innerRadius = am4core.percent(40);
        chart.startAngle = 180;
        chart.endAngle = 360;

        const series = chart.series.push(new am4charts.PieSeries());
        series.dataFields.value = 'frequency';
        series.dataFields.category = 'issueType';
        series.labels.template.text = '{issueType}s';
        series.labels.template.tooltipText = '{frequency} {issueType}s';
        series.slices.template.tooltipText = '{category}: {frequency}';
        series.legendSettings.labelText = '[bold {color}]{issueType}[/]';

        series.slices.template.cornerRadius = 7;
        series.slices.template.innerCornerRadius = 4;
        series.slices.template.draggable = true;
        series.slices.template.inert = true;
        series.alignLabels = false;

        series.hiddenState.properties.startAngle = 90;
        series.hiddenState.properties.endAngle = 90;
    }

    ngOnDestroy() {
        this.zone.runOutsideAngular(() => {
            if (this.chart) {
                this.chart.dispose();
            }
        });
    }

    private createCharts(ppmcProjectsWorklog: any) {
        this.createWorklogChart(ppmcProjectsWorklog);
        this.createFrequancyChart(ppmcProjectsWorklog);
    }
}
