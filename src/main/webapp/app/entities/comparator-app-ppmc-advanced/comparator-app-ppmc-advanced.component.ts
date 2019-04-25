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
import { CollaboratorDailyImputation, ICollaboratorDailyImputation } from 'app/shared/model/collaborator-daily-imputation.model';
import { ICollaborator } from 'app/shared/model/collaborator.model';
import { GapModel } from 'app/shared/model/gap.model';

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
    private isNewUpload? = true;
    private currentAccount: any;
    private myTeam: ITeam;
    private allTeams: ITeam[];
    private currentYear: number = new Date().getFullYear();
    private currentMonth: number = new Date().getMonth() + 1;
    private years: Array<number> = [];
    private months: Array<number> = [];
    private imputationDays: Array<number>;
    private numberOfDaysOfCurrentMonth: number = new Date(this.currentYear, this.currentMonth, 0).getDate();
    private notifiableCollabs: Map<ICollaborator, Array<number>> = new Map<ICollaborator, Array<number>>();
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
                        this.initializeDays();
                        this.initNotifiableCollabs();
                    }
                },
                error => {
                    console.log(error);
                }
            );
        } else {
            this.comparatorAppPpmcAdvancedService.getAdvancedComparisonFromDB(this.appRequestBody).subscribe(res => {
                this.comparator = res.body;
                this.initializeDays();
                this.initNotifiableCollabs();
            });
        }
        this.selectedFiles = undefined;
    }

    private initNotifiableCollabs() {
        this.comparator.forEach(element => {
            this.imputationDays.forEach(day => {
                if (this.isWillBeColored(element, day)) {
                    if (this.notifiableCollabs.has(element.collaborator)) {
                        this.notifiableCollabs.get(element.collaborator).push(day);
                    } else {
                        this.notifiableCollabs.set(element.collaborator, [day]);
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

    private isUndefined(daily): boolean {
        return daily === undefined;
    }

    private getColor(element: IImputationComparatorAdvancedDTO, day: number): string {
        if (this.notifiableCollabs.get(element.collaborator)) {
            if (this.notifiableCollabs.get(element.collaborator).find(item => item === day) !== undefined) {
                return '#feabab';
            }
        }
    }

    private initializeNotification(collabElement?: any) {
        this.notifications = [];
        if (collabElement) {
            this.initNotificationForCollab(collabElement);
        } else {
            this.comparator.forEach(element => {
                if (this.notifiableCollabs.has(element.collaborator)) {
                    this.initNotificationForCollab(element);
                }
            });
        }
    }

    private initNotificationForCollab(element) {
        const appGap = new GapModel('APP', []);
        const comparedGap = new GapModel('PPMC', []);
        const appDailies: ICollaboratorDailyImputation[] = [];
        const comparedDailies: ICollaboratorDailyImputation[] = [];
        appGap.dailyImputations = appDailies;
        comparedGap.dailyImputations = comparedDailies;
        this.notifications.push(
            new NotificationModel(element.collaborator, this.appRequestBody.month, this.appRequestBody.year, appGap, comparedGap)
        );
    }

    private notifyCollabsWithGap(collabElement?: any) {
        if (collabElement) {
            this.initializeNotification(collabElement);
            this.notifySingleCollab(collabElement);
        } else {
            this.initializeNotification();
            this.comparator.forEach(element => {
                this.notifySingleCollab(element);
            });
        }
        console.log(this.notifications);
        this.comparatorAppPpmcAdvancedService.sendNotifications(this.notifications).subscribe(res => {
            console.log(res.body);
        });
    }

    private notifySingleCollab(element) {
        if (this.notifiableCollabs.has(element.collaborator)) {
            this.notifiableCollabs.get(element.collaborator).forEach(day => {
                let appDaily = this.findDailyImputation(element.appMonthlyImputation, day);
                let comparedDaily = this.findDailyImputation(element.comparedMonthlyImputation, day);
                if (this.isOneUndefined(appDaily, comparedDaily)) {
                    if (this.isUndefined(appDaily)) {
                        appDaily = this.initUndefinedDaily(comparedDaily);
                    } else {
                        comparedDaily = this.initUndefinedDaily(appDaily);
                    }
                }
                this.updateNotification(element.collaborator, appDaily, comparedDaily);
            });
        }
    }

    private updateNotification(
        collaborator: ICollaborator,
        appDaily: ICollaboratorDailyImputation,
        comparedDaily: ICollaboratorDailyImputation
    ) {
        const notification = this.findNotificationByCollab(collaborator);
        notification.appGap.dailyImputations.push(appDaily);
        notification.comparedGap.dailyImputations.push(comparedDaily);

        const index = this.notifications.indexOf(notification);
        this.notifications[index] = notification;
    }

    private findNotificationByCollab(collaborator: ICollaborator): INotificationModel {
        return this.notifications.find(notif => notif.collaborator.id === collaborator.id);
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
        let lastMonthInYear = 12;
        this.months = [];
        if (this.appRequestBody.year == this.currentYear) {
            lastMonthInYear = this.currentMonth;
        }
        for (let i = 1; i <= lastMonthInYear; i++) {
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

    private initUndefinedDaily(source: ICollaboratorDailyImputation) {
        return this.createDaily(source);
    }

    private createDaily(source: ICollaboratorDailyImputation): ICollaboratorDailyImputation {
        const dest = new CollaboratorDailyImputation();
        dest.day = source.day;
        dest.charge = 0;
        return dest;
    }

    private setIsNewUpload(predicate: string) {
        this.isNewUpload = predicate === 'false' ? false : true;
    }
}
