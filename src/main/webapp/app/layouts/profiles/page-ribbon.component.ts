import { Component, OnInit } from '@angular/core';
import { ProfileService } from './profile.service';
import { ProfileInfo } from './profile-info.model';

@Component({
    selector: 'jhi-page-ribbon',
    template: `
        <div class="ribbon" *ngIf="ribbonEnv">
            <a href="" jhiTranslate="global.ribbon.{{ ribbonEnv }}">{{ ribbonEnv }}</a>
        </div>
        <ngx-loading-bar
            [color]="'#907d4c'"
            [includeBar]="true"
            [includeSpinner]="true"
            [fixed]="true"
            [height]="'5px'"
            [diameter]="'30px'"
        ></ngx-loading-bar>
    `,
    styleUrls: ['page-ribbon.css']
})
export class PageRibbonComponent implements OnInit {
    profileInfo: ProfileInfo;
    ribbonEnv: string;

    constructor(private profileService: ProfileService) {}

    ngOnInit() {
        this.profileService.getProfileInfo().then(profileInfo => {
            this.profileInfo = profileInfo;
            this.ribbonEnv = profileInfo.ribbonEnv;
        });
    }
}
