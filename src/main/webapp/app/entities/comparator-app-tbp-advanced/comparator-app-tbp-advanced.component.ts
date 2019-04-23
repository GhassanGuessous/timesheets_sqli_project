import { Component, OnInit } from '@angular/core';
import { ITeam } from 'app/shared/model/team.model';
import { AccountService } from 'app/core';
import { TeamService } from 'app/entities/team';
import { ComparatorAppTbpAdvancedService } from 'app/entities/comparator-app-tbp-advanced/comparator-app-tbp-advanced.service';
import { AppTbpRequestBody } from 'app/shared/model/app-tbp-request-body';
import { ICollaborator } from 'app/shared/model/collaborator.model';
import { ICollaboratorDailyImputation } from 'app/shared/model/collaborator-daily-imputation.model';
import { INotificationModel, NotificationModel } from 'app/shared/model/notification.model';
import { IImputationComparatorAdvancedDTO } from 'app/shared/model/imputation-comparator-advanced-dto.model';

@Component({
    selector: 'jhi-comparator-app-tbp-advanced',
    templateUrl: './comparator-app-tbp-advanced.component.html',
    styles: []
})
export class ComparatorAppTbpAdvancedComponent implements OnInit {
    private currentAccount: any;
    private myTeam: ITeam;
    private allTeams: Array<ITeam>;
    private currentYear: number = new Date().getFullYear();
    private currentMonth: number = new Date().getMonth() + 1;
    private years: Array<number> = [];
    private months: Array<number>;
    private predicate: any;
    private reverse: any;
    private comparator: any;
    private imputationDays: Array<number>;
    private notifiableCollabs: Map<ICollaborator, Array<number>> = new Map<ICollaborator, Array<number>>();
    private notifications?: INotificationModel[];
    private appTbpRequestBody: AppTbpRequestBody = new AppTbpRequestBody(null, this.currentYear, this.currentMonth);

    constructor(
        protected service: ComparatorAppTbpAdvancedService,
        protected accountService: AccountService,
        protected teamService: TeamService
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

    private loadAll() {
        this.teamService.findAllTeamsWithoutPagination().subscribe(res => {
            this.allTeams = res.body;
        });
    }

    private loadDelcoTeam(id: bigint) {
        this.teamService.findByDelco(id).subscribe(data => {
            this.myTeam = data.body;
            this.appTbpRequestBody.team = this.myTeam;
        });
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
        if (this.appTbpRequestBody.year == this.currentYear) {
            lastYear = this.currentMonth;
        }
        for (let i = 1; i <= lastYear; i++) {
            this.months.push(i);
        }
    }

    private compare() {
        this.service.compare(this.appTbpRequestBody).subscribe(res => {
            this.comparator = res.body;
            this.initializeDays();
            this.initNotifiableCollabs();
        });
    }

    private initializeDays() {
        this.imputationDays = [];
        this.addDaysFromMonthlyImputation();
        this.removeDuplecates();
    }

    private addDaysFromMonthlyImputation() {
        this.comparator.forEach(element => {
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

    private removeDuplecates() {
        this.imputationDays = Array.from(new Set(this.imputationDays)).sort((a, b) => a - b);
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
            const appDailyImputation = this.findDailyImputation(element.appMonthlyImputation, day);
            const comparedDailyImputation = this.findDailyImputation(element.comparedMonthlyImputation, day);
            if (appDailyImputation && comparedDailyImputation) {
                if (appDailyImputation.charge !== comparedDailyImputation.charge) {
                    return true;
                }
            } else if (this.isOneUndefined(appDailyImputation, comparedDailyImputation)) {
                return true;
            }
        }
    }

    private findDailyImputation(appMonthlyImputation: any, day: number) {
        return appMonthlyImputation.dailyImputations.find(daily => daily.day === day);
    }

    private isOneUndefined(appDailyImputation, comparedDailyImputation) {
        return (!appDailyImputation && comparedDailyImputation) || (appDailyImputation && !comparedDailyImputation);
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
        const gapMap = new Map<string, ICollaboratorDailyImputation[]>();
        const appDailies: ICollaboratorDailyImputation[] = [];
        const comparedDailies: ICollaboratorDailyImputation[] = [];
        gapMap.set('app', appDailies);
        gapMap.set('ppmc', comparedDailies);
        this.notifications.push(new NotificationModel(element.collaborator, gapMap));
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
    }

    private notifySingleCollab(element) {
        if (this.notifiableCollabs.has(element.collaborator)) {
            this.notifiableCollabs.get(element.collaborator).forEach(day => {
                const appDaily = this.findDailyImputation(element.appMonthlyImputation, day);
                const comparedDaily = this.findDailyImputation(element.comparedMonthlyImputation, day);
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
        notification.gapMap.get('app').push(appDaily);
        notification.gapMap.get('ppmc').push(comparedDaily);

        const index = this.notifications.indexOf(notification);
        this.notifications[index] = notification;
    }

    private findNotificationByCollab(collaborator: ICollaborator): INotificationModel {
        return this.notifications.find(notif => notif.collaborator.id === collaborator.id);
    }
}
