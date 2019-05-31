import { Component, OnInit } from '@angular/core';
import { ITeam } from 'app/shared/model/team.model';
import { AccountService } from 'app/core';
import { TeamService } from 'app/entities/team';
import { ComparatorAppTbpAdvancedService } from 'app/entities/comparator-app-tbp-advanced/comparator-app-tbp-advanced.service';
import { AppTbpRequestBody, IAppTbpRequestBody } from 'app/shared/model/app-tbp-request-body';
import { ICollaborator } from 'app/shared/model/collaborator.model';
import { CollaboratorDailyImputation, ICollaboratorDailyImputation } from 'app/shared/model/collaborator-daily-imputation.model';
import { INotificationModel, NotificationModel } from 'app/shared/model/notification.model';
import { IImputationComparatorAdvancedDTO } from 'app/shared/model/imputation-comparator-advanced-dto.model';
import { GapModel } from 'app/shared/model/gap.model';
import { AuthTbpModalService } from 'app/core/authTbp/auth-tbp-modal.service';

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
    private appTbpRequestBody: IAppTbpRequestBody = new AppTbpRequestBody(null, this.currentYear, this.currentMonth);

    constructor(
        protected service: ComparatorAppTbpAdvancedService,
        protected accountService: AccountService,
        protected teamService: TeamService,
        private authTbpModalService: AuthTbpModalService
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
        const startImputationsYear = 2015;
        for (let i = startImputationsYear; i <= this.currentYear; i++) {
            this.years.push(i);
        }
    }

    private initializeMonth() {
        let lastMonthInYear = 12;
        this.months = [];
        if (this.appTbpRequestBody.year == this.currentYear) {
            lastMonthInYear = this.currentMonth;
        }
        for (let i = 1; i <= lastMonthInYear; i++) {
            this.months.push(i);
        }
    }

    private compare() {
        if (localStorage.getItem('isTbpAuthenticated') === 'false') {
            this.authenticateThenCompare();
        } else {
            this.compareWhenIsAlreadyAuthenticated();
        }
    }

    private authenticateThenCompare() {
        this.appTbpRequestBody.requestType = 'APP_TBP_ADVANCED_COMPARATOR';
        this.authTbpModalService.open(this.appTbpRequestBody).then(
            result => {
                this.comparator = result;
                this.initializeDays();
                this.initNotifiableCollabs();
            },
            reason => {
                console.log(reason);
            }
        );
    }

    private compareWhenIsAlreadyAuthenticated() {
        this.appTbpRequestBody.username = localStorage.getItem('username');
        this.appTbpRequestBody.password = localStorage.getItem('password');
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
                if (this.isDayWithGap(element, day)) {
                    if (this.notifiableCollabs.has(element.collaborator)) {
                        this.notifiableCollabs.get(element.collaborator).push(day);
                    } else {
                        this.notifiableCollabs.set(element.collaborator, [day]);
                    }
                }
            });
        });
    }

    private isDayWithGap(element: any, day: number): boolean {
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
        const comparedGap = new GapModel('TBP', []);
        const appDailies: ICollaboratorDailyImputation[] = [];
        const comparedDailies: ICollaboratorDailyImputation[] = [];
        appGap.dailyImputations = appDailies;
        comparedGap.dailyImputations = comparedDailies;
        this.notifications.push(
            new NotificationModel(element.collaborator, this.appTbpRequestBody.month, this.appTbpRequestBody.year, appGap, comparedGap)
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
        this.service.sendNotifications(this.notifications).subscribe();
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

    private initUndefinedDaily(source: ICollaboratorDailyImputation) {
        return this.createDaily(source);
    }

    private createDaily(source: ICollaboratorDailyImputation): ICollaboratorDailyImputation {
        const dest = new CollaboratorDailyImputation();
        dest.day = source.day;
        dest.charge = 0;
        return dest;
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

    private isFilledImputation(): boolean {
        return this.comparator !== undefined;
    }
}
