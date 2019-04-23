import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ITeam } from 'app/shared/model/team.model';
import { AppRequestBody, IAppRequestBody } from 'app/shared/model/app-request-body';
import { ComparatorAppPpmcAdvancedService } from 'app/entities/comparator-app-ppmc-advanced/comparator-app-ppmc-advanced.service';
import { AccountService } from 'app/core';
import { TeamService } from 'app/entities/team';
import { IImputationComparatorAdvancedDTO } from 'app/shared/model/imputation-comparator-advanced-dto.model';
import { INotificationModel, NotificationModel } from 'app/shared/model/notification.model';
import { ICollaboratorMonthlyImputation } from 'app/shared/model/collaborator-monthly-imputation.model';
import { ICollaboratorDailyImputation } from 'app/shared/model/collaborator-daily-imputation.model';
import { ICollaborator } from 'app/shared/model/collaborator.model';

@Component({
    selector: 'jhi-comparator-app-ppmc-advanced',
    templateUrl: './comparator-app-ppmc-advanced.component.html',
    styles: []
})
export class ComparatorAppPpmcAdvancedComponent implements OnInit {
    private selectedFiles: FileList;
    private currentFileUpload: File;
    private resultBody?: any;
    private comparator?: IImputationComparatorAdvancedDTO[];
    private notifications?: INotificationModel[];
    private currentAccount: any;
    private myTeam: ITeam;
    private allTeams: ITeam[];
    private currentYear: number = new Date().getFullYear();
    private currentMonth: number = new Date().getMonth() + 1;
    private years: Array<number> = [];
    private months: Array<number> = [];
    private imputationDays: Array<number>;
    private numberOfDaysOfCurrentMonth: number = new Date(this.currentYear, this.currentMonth, 0).getDate();
    private isCollabNotifiable: Map<ICollaborator, Array<number>> = new Map<ICollaborator, Array<number>>();
    private appRequestBody: IAppRequestBody = new AppRequestBody(
        '',
        this.currentYear,
        this.currentMonth,
        1,
        this.numberOfDaysOfCurrentMonth
    );

    constructor(
        protected accountService: AccountService,
        protected teamService: TeamService,
        protected comparatorAppPpmcAdvancedService: ComparatorAppPpmcAdvancedService
    ) {}

    ngOnInit() {
        this.initialize();
        this.accountService.identity().then(account => {
            this.currentAccount = account;
            if (this.isAdmin()) {
                this.loadAll();
            } else {
                this.loadDelcoTeam(account.id);
            }
        });
    }

    private selectFile(event) {
        this.selectedFiles = event.target.files;
    }

    private loadAll() {
        this.teamService.findAllTeamsWithoutPagination().subscribe(res => {
            this.allTeams = res.body;
        });
    }

    private loadDelcoTeam(id: bigint) {
        this.teamService.findByDelco(id).subscribe(data => {
            this.myTeam = data.body;
            this.appRequestBody.agresso = this.myTeam.agresso;
        });
    }

    private compare() {
        if (this.selectedFiles !== undefined) {
            this.currentFileUpload = this.selectedFiles.item(0);
            this.comparatorAppPpmcAdvancedService.getAdvancedComparison(this.currentFileUpload, this.appRequestBody).subscribe(
                event => {
                    if (event instanceof HttpResponse) {
                        this.resultBody = event.body;
                        this.comparator = this.resultBody;
                        console.log(this.comparator);
                        this.initializeNotification();
                        console.log(this.notifications);
                        this.initializeDays();
                        this.initNotifiableCollabs();
                    }
                },
                error => {
                    console.log(error);
                }
            );
        }
        this.selectedFiles = undefined;
    }

    private initNotifiableCollabs() {
        this.comparator.forEach(element => {
            this.imputationDays.forEach(day => {
                if (this.isWillBeColored(element, day)) {
                    if (this.isCollabNotifiable.has(element.collaborator)) {
                        this.isCollabNotifiable.get(element.collaborator).push(day);
                    } else {
                        this.isCollabNotifiable.set(element.collaborator, [day]);
                    }
                }
            });
        });
    }

    private isWillBeColored(element: any, day: number): boolean {
        if (element.appMonthlyImputation && element.comparedMonthlyImputation) {
            const appDaily = this.findDailyImputation(element.appMonthlyImputation, day);
            const comparedDaily = this.findDailyImputation(element.comparedMonthlyImputation, day);
            if (appDaily && comparedDaily) {
                return this.getColorWhenDifferentCharge(appDaily, comparedDaily);
            } else {
                if (this.isOneUndefined(appDaily, comparedDaily)) {
                    return this.getDefinedOne(appDaily, comparedDaily).charge !== 0;
                }
            }
            return false;
        }
    }

    private getColor(element: IImputationComparatorAdvancedDTO, day: number): string {
        if (this.isCollabNotifiable.get(element.collaborator)) {
            if (this.isCollabNotifiable.get(element.collaborator).find(item => item == day) !== undefined) {
                return '#feabab';
            }
        }
    }

    private findDailyImputation(monthlyImputation: ICollaboratorMonthlyImputation, day: number): any {
        const appDaily = monthlyImputation.dailyImputations.find(daily => daily.day === day);
        return appDaily;
    }

    private getColorWhenDifferentCharge(appDaily: ICollaboratorDailyImputation, comparedDaily: ICollaboratorDailyImputation): boolean {
        if (appDaily.charge !== comparedDaily.charge) {
            return true;
        }
    }

    private isOneUndefined(appDaily, comparedDaily): boolean {
        return (!appDaily && comparedDaily) || (appDaily && !comparedDaily);
    }

    private getDefinedOne(appDaily, comparedDaily) {
        return appDaily ? appDaily : comparedDaily;
    }

    private notifyCollabsWithGap() {
        this.imputationDays.forEach(day => {
            this.comparator.forEach(element => {
                this.notifySingleCollab(element, day);
            });
        });
        console.log(this.notifications);
    }

    private notifySingleCollab(element, day) {
        if (element.appMonthlyImputation && element.comparedMonthlyImputation) {
            const appDaily = this.findDailyImputation(element.appMonthlyImputation, day);
            const comparedDaily = this.findDailyImputation(element.comparedMonthlyImputation, day);
            if (appDaily && comparedDaily) {
                if (appDaily.charge !== comparedDaily.charge) {
                    this.updateNotification(element.collaborator, appDaily, comparedDaily);
                }
            } else {
                if (this.isOneUndefined(appDaily, comparedDaily)) {
                    let definedDaily = this.getDefinedOne(appDaily, comparedDaily);
                    if (definedDaily.charge !== 0) {
                        definedDaily.collaboratorMonthlyImputation.imputation.imputationType.name.toUpperCase() === 'APP'
                            ? this.updateNotification(element.collaborator, definedDaily, null)
                            : this.updateNotification(element.collaborator, null, definedDaily);
                    }
                }
            }
        }
    }

    private updateNotification(
        collaborator: ICollaborator,
        appDaily: ICollaboratorDailyImputation,
        comparedDaily: ICollaboratorDailyImputation
    ) {
        let notification = this.findNotificationByCollab(collaborator);
        notification.gapMap.get('app').push(appDaily);
        notification.gapMap.get('ppmc').push(comparedDaily);

        const index = this.notifications.indexOf(notification);
        this.notifications[index] = notification;
    }

    private findNotificationByCollab(collaborator: ICollaborator): INotificationModel {
        return this.notifications.find(notif => notif.collaborator.id == collaborator.id);
    }

    private isAdmin() {
        return this.currentAccount.authorities.includes('ROLE_ADMIN');
    }

    private initialize() {
        this.initializeYears();
        this.initializeMonth();
    }

    private initializeYears() {
        for (let i = 2015; i <= this.currentYear; i++) {
            this.years.push(i);
        }
    }

    private initializeMonth() {
        let lastYear = 12;
        this.months = [];
        if (this.appRequestBody.year == this.currentYear) {
            lastYear = this.currentMonth;
        }
        for (let i = 1; i <= lastYear; i++) {
            this.months.push(i);
        }
    }

    private initializeDays() {
        this.imputationDays = [];
        this.addDaysFromMonthlyImputation();
        this.removeDuplicates();
    }

    private addDaysFromMonthlyImputation() {
        this.resultBody.forEach(element => {
            if (element.appMonthlyImputation) {
                element.appMonthlyImputation.dailyImputations.forEach(daily => {
                    this.imputationDays.push(daily.day);
                });
                element.comparedMonthlyImputation.dailyImputations.forEach(daily => {
                    this.imputationDays.push(daily.day);
                });
            }
        });
    }

    private removeDuplicates() {
        this.imputationDays = Array.from(new Set(this.imputationDays)).sort((a, b) => a - b);
    }

    private initializeNotification() {
        this.notifications = [];
        this.comparator.forEach(element => {
            let gapMap = new Map<string, ICollaboratorDailyImputation[]>();
            let appDailies: ICollaboratorDailyImputation[] = [];
            let comparedDailies: ICollaboratorDailyImputation[] = [];
            gapMap.set('app', appDailies);
            gapMap.set('ppmc', comparedDailies);
            this.notifications.push(new NotificationModel(element.collaborator, gapMap));
        });
    }
}
