import { AfterViewInit, Component, ElementRef, NgZone, OnDestroy, OnInit, ViewChild } from '@angular/core';
import { ITeam } from 'app/shared/model/team.model';
import { ITeamYearRequest, TeamYearRequest } from 'app/shared/model/team-year-request.model';
import { AccountService } from 'app/core';
import { TeamService } from 'app/entities/team';
import { GapPerTeamStatisticsService } from 'app/entities/gap-per-team-statistics/gap-per-team-statistics.service';
import * as am4core from '@amcharts/amcharts4/core';
import * as am4charts from '@amcharts/amcharts4/charts';
import am4themes_animated from '@amcharts/amcharts4/themes/animated';

am4core.useTheme(am4themes_animated);

@Component({
    selector: 'jhi-gap-variation-statistics',
    templateUrl: './gap-variation-statistics.component.html',
    styles: []
})
export class GapVariationStatisticsComponent implements OnInit, AfterViewInit, OnDestroy {
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
        protected statisticsService: GapPerTeamStatisticsService,
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

    private createChart(data) {
        const htmlElement = this.chartDiv.nativeElement;
        const chart = am4core.create(htmlElement, am4charts.XYChart);
        chart.data = data;

        // Create axes
        const categoryAxis = this.getCategoryAxis(chart);
        const valueAxis = this.getValueAxis(chart);

        // Create series
        const series = this.createSeries(chart);

        // Drop-shaped tooltips
        this.addDropShapedToolpitsToSeries(series);

        // Make bullets grow on hover
        this.makeBulletsGrowOnHover(series);

        // Make a panning cursor
        this.makePanningCursor(chart, series, categoryAxis);

        // Create vertical scrollbar and place it before the value axis
        this.createScrollBar(chart);

        this.chart = chart;
    }

    private getValueAxis(chart) {
        const valueAxis = chart.yAxes.push(new am4charts.ValueAxis());
        valueAxis.title.text = 'Notification frequency';
        valueAxis.renderer.opposite = false;
    }

    private getCategoryAxis(chart) {
        const categoryAxis = chart.xAxes.push(new am4charts.CategoryAxis());
        categoryAxis.dataFields.category = 'month';
        categoryAxis.title.text = 'Months';
        categoryAxis.renderer.minGridDistance = 50;
        categoryAxis.renderer.grid.template.location = 0.5;
    }

    // Create series
    createSeries(chart) {
        const series = chart.series.push(new am4charts.LineSeries());
        series.dataFields.valueY = 'frequence';
        series.dataFields.categoryX = 'month';
        series.tooltipText = '{frequence}';
        series.strokeWidth = 2;
        series.minBulletDistance = 15;
        return series;
    }

    private addDropShapedToolpitsToSeries(series: any) {
        series.tooltip.background.cornerRadius = 20;
        series.tooltip.background.strokeOpacity = 0;
        series.tooltip.pointerOrientation = 'vertical';
        series.tooltip.label.minWidth = 40;
        series.tooltip.label.minHeight = 40;
        series.tooltip.label.textAlign = 'middle';
        series.tooltip.label.textValign = 'middle';
    }

    private makeBulletsGrowOnHover(series: any) {
        const bullet = series.bullets.push(new am4charts.CircleBullet());
        bullet.circle.strokeWidth = 2;
        bullet.circle.radius = 4;
        bullet.circle.fill = am4core.color('#fff');

        const bullethover = bullet.states.create('hover');
        bullethover.properties.scale = 1.3;
    }

    private makePanningCursor(chart, series: any, dateAxis: any) {
        chart.cursor = new am4charts.XYCursor();
        chart.cursor.behavior = 'panXY';
        chart.cursor.xAxis = dateAxis;
        chart.cursor.snapToSeries = series;
    }

    private createScrollBar(chart) {
        chart.scrollbarY = new am4core.Scrollbar();
        chart.scrollbarY.parent = chart.leftAxesContainer;
        chart.scrollbarY.toBack();
    }

    private getStatistics() {
        this.notifications = this.statisticsService.getNotificationsGapVariation(this.teamYearRequest).subscribe(data => {
            this.notifications = data.body;
            this.zone.runOutsideAngular(() => {
                this.createChart(this.notifications);
            });
        });
    }

    ngOnDestroy() {
        this.zone.runOutsideAngular(() => {
            if (this.chart) {
                this.chart.dispose();
            }
        });
    }
}
