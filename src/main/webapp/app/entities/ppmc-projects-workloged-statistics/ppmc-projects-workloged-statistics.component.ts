import { Component, ElementRef, NgZone, OnDestroy, OnInit, ViewChild } from '@angular/core';
import * as am4charts from '@amcharts/amcharts4/charts';
import { ITeam } from 'app/shared/model/team.model';
import { AccountService } from 'app/core';
import { TeamService } from 'app/entities/team';
import * as am4core from '@amcharts/amcharts4/core';
import { PpmcProjectsWorklogedStatisticsService } from 'app/entities/ppmc-projects-workloged-statistics/ppmc-projects-workloged-statistics.service';
import { AuthJiraModalService } from 'app/core/authJira/auth-jira-modal.service';
import { ITbpRequestBody, TbpRequestBody } from 'app/shared/model/tbp-request-body';
import { DatePipe } from '@angular/common';
import am4themes_animated from '@amcharts/amcharts4/themes/animated';
import am4themes_dataviz from '@amcharts/amcharts4/themes/dataviz';
import am4themes_moonrisekingdom from '@amcharts/amcharts4/themes/moonrisekingdom';

// am4core.useTheme(am4themes_dataviz);
am4core.useTheme(am4themes_animated);

@Component({
    selector: 'jhi-ppmc-prjects-workloged-statistics',
    templateUrl: './ppmc-projects-workloged-statistics.component.html',
    styles: []
})
export class PpmcProjectsWorklogedStatisticsComponent implements OnInit, OnDestroy {
    @ViewChild('worklogsChart') worklogsChart: ElementRef;
    @ViewChild('frequancyChart') frequancyChart: ElementRef;

    private chart: am4charts.XYChart;
    private ppmcProjectsWorklog: any;
    requestBody: ITbpRequestBody = new TbpRequestBody();
    private currentAccount: any;
    private allTeams: ITeam[];
    private myTeam: ITeam;

    constructor(
        protected accountService: AccountService,
        protected teamService: TeamService,
        protected ppmcPrjectsWorklogedStatisticsService: PpmcProjectsWorklogedStatisticsService,
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
        this.requestBody.requestType = 'JIRA_PPMC_PROJECT_WORKLOGED_STATISTICS';
        this.authJiraModalService.open(this.requestBody).then(
            result => {
                this.ppmcProjectsWorklog = result;
                console.log(result);
                this.zone.runOutsideAngular(() => {
                    this.createCharts(this.ppmcProjectsWorklog);
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
        this.ppmcProjectsWorklog = this.ppmcPrjectsWorklogedStatisticsService.getPpmcProjetctsWorklog(this.requestBody).subscribe(data => {
            this.ppmcProjectsWorklog = data.body;
            console.log(data.body);
            this.zone.runOutsideAngular(() => {
                this.createCharts(this.ppmcProjectsWorklog);
            });
        });
    }

    private createWorklogChart(data) {
        const htmlElement = this.worklogsChart.nativeElement;
        const chart = am4core.create(htmlElement, am4charts.PieChart);
        chart.data = data;

        chart.legend = new am4charts.Legend();
        chart.hiddenState.properties.opacity = 0;

        const series = chart.series.push(new am4charts.PieSeries());
        series.dataFields.value = 'totalMinutes';
        series.dataFields.category = 'ppmcProject';
        series.labels.template.text = '';
        series.ticks.template.disabled = true;
        series.labels.template.tooltipText = '{ppmcProject}';
        series.slices.template.tooltipText = '{category}: {timeSpent}';
        series.legendSettings.labelText = '[bold {color}]{timeSpent}[/]';
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
        series.dataFields.category = 'ppmcProject';
        series.labels.template.text = '';
        series.ticks.template.disabled = true;
        series.labels.template.tooltipText = '{ppmcProject}';
        series.slices.template.tooltipText = '{category}: {frequency}';
        series.legendSettings.labelText = '[bold font-size:10px {color}]{ppmcProject}[/]';

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
