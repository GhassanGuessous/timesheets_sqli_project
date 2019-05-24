import { Component, OnInit, NgZone, ViewChild, ElementRef, AfterViewInit, OnDestroy } from '@angular/core';
import { AccountService } from 'app/core';
import { TeamService } from 'app/entities/team';
import { ITeam } from 'app/shared/model/team.model';

import * as am4core from '@amcharts/amcharts4/core';
import * as am4charts from '@amcharts/amcharts4/charts';
import am4themes_animated from '@amcharts/amcharts4/themes/animated';
import { ITeamYearRequest, TeamYearRequest } from 'app/shared/model/team-year-request.model';
import { StatisticsService } from 'app/entities/statistics/statistics.service';

am4core.useTheme(am4themes_animated);

@Component({
    selector: 'jhi-statistics',
    templateUrl: './statistics.component.html',
    styles: []
})
export class StatisticsComponent implements OnInit, AfterViewInit, OnDestroy {
    @ViewChild('chartdiv') chartDiv: ElementRef;

    private chart: am4charts.XYChart;
    private notifications: any;
    data: any;
    private years: Array<number> = [];
    private currentYear: number = new Date().getFullYear();
    private currentAccount: any;
    private allTeams: ITeam[];
    private myTeam: ITeam;
    private teamYearRequest: ITeamYearRequest = new TeamYearRequest(null, this.currentYear);

    constructor(
        protected accountService: AccountService,
        protected teamService: TeamService,
        protected statisticsService: StatisticsService,
        private zone: NgZone
    ) {}

    ngOnInit() {
        this.initializeYears();
        this.accountService.identity().then(account => {
            this.currentAccount = account;
            if (this.isAdmin()) {
                this.loadAll();
            } else {
                this.loadDelcoTeam(account.id);
            }
        });
    }

    ngAfterViewInit() {
        setTimeout(() => {
            this.getStatistics();
        }, 1000);
    }

    private initializeYears() {
        const startImputationsYear = 2015;
        for (let i = startImputationsYear; i <= this.currentYear; i++) {
            this.years.push(i);
        }
    }

    private loadAll() {
        this.teamService.findAllTeamsWithoutPagination().subscribe(res => {
            this.allTeams = res.body;
        });
    }

    private loadDelcoTeam(id: bigint) {
        this.teamService.findByDelco(id).subscribe(data => {
            this.myTeam = data.body;
            this.teamYearRequest.team = this.myTeam;
            this.teamYearRequest.year = this.currentYear;
        });
    }

    private isAdmin() {
        return this.currentAccount.authorities.includes('ROLE_ADMIN');
    }

    private getStatistics() {
        this.notifications = this.statisticsService.findTeamNotifications(this.teamYearRequest).subscribe(data => {
            this.notifications = data.body;
            this.zone.runOutsideAngular(() => {
                this.createChart(this.notifications);
            });
        });
    }

    private createChart(data) {
        const htmlElement = this.chartDiv.nativeElement;
        const chart = am4core.create(htmlElement, am4charts.XYChart);
        chart.data = data;

        this.getValueAxis(chart);
        this.getCategoryAxis(chart);

        this.chart = chart;

        this.createSeries('app_vs_ppmc', 'APP-vs-PPMC');
        this.createSeries('app_vs_tbp', 'APP-vs-TBP');

        // Add cursor
        chart.cursor = new am4charts.XYCursor();
        // Add legend
        chart.legend = new am4charts.Legend();
    }

    private getValueAxis(chart) {
        const valueAxis = chart.xAxes.push(new am4charts.ValueAxis());
        valueAxis.title.text = 'Notification frequency';
        valueAxis.renderer.opposite = true;
    }

    private getCategoryAxis(chart) {
        const categoryAxis = chart.yAxes.push(new am4charts.CategoryAxis());
        categoryAxis.dataFields.category = 'collaborator';
        categoryAxis.title.text = 'Collaborators';
        categoryAxis.numberFormatter.numberFormat = '#';
        categoryAxis.renderer.inversed = true;
        categoryAxis.renderer.grid.template.location = 0;
        categoryAxis.renderer.cellStartLocation = 0.1;
        categoryAxis.renderer.cellEndLocation = 0.9;
    }

    // Create series
    createSeries(field, name) {
        const series = this.chart.series.push(new am4charts.ColumnSeries());
        series.dataFields.valueX = field;
        series.dataFields.categoryY = 'collaborator';
        series.name = name;
        series.columns.template.tooltipText = '{name}: [bold]{valueX}[/]';
        series.columns.template.height = am4core.percent(100);
        series.sequencedInterpolation = true;

        const valueLabel = series.bullets.push(new am4charts.LabelBullet());
        valueLabel.label.text = '{valueX}';
        valueLabel.label.horizontalCenter = 'left';
        valueLabel.label.dx = 10;
        valueLabel.label.hideOversized = false;
        valueLabel.label.truncate = false;

        const categoryLabel = series.bullets.push(new am4charts.LabelBullet());
        // categoryLabel.label.text = "{name}";
        categoryLabel.label.horizontalCenter = 'right';
        categoryLabel.label.dx = -10;
        categoryLabel.label.fill = am4core.color('#fff');
        categoryLabel.label.hideOversized = false;
        categoryLabel.label.truncate = false;
    }

    ngOnDestroy() {
        this.zone.runOutsideAngular(() => {
            if (this.chart) {
                this.chart.dispose();
            }
        });
    }
}
