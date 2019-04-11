import { Component, OnInit } from '@angular/core';
import { TimesheetPpmcService } from '../timesheet-ppmc/timesheet-ppmc.service';
import { HttpResponse, HttpEventType } from '@angular/common/http';
import { IImputation } from 'app/shared/model/imputation.model';

@Component({
    selector: 'jhi-timesheet-ppmc',
    templateUrl: './timesheet-ppmc.component.html',
    styles: []
})
export class TimesheetPpmcComponent implements OnInit {
    selectedFiles: FileList;
    currentFileUpload: File;
    imputation: IImputation;
    private days: Array<number> = [];
    progress: { percentage: number } = { percentage: 0 };

    constructor(private timesheetPpmcService: TimesheetPpmcService) {}

    ngOnInit() {}

    selectFile(event) {
        this.selectedFiles = event.target.files;
    }

    upload() {
        this.progress.percentage = 0;
        this.currentFileUpload = this.selectedFiles.item(0);
        this.timesheetPpmcService.pushFileToStorage(this.currentFileUpload).subscribe(
            event => {
                if (event.type === HttpEventType.UploadProgress) {
                    this.progress.percentage = Math.round((100 * event.loaded) / event.total);
                } else if (event instanceof HttpResponse) {
                    this.imputation = JSON.parse(event.body.toString());
                    this.initializeDays(this.imputation);
                }
            },
            error => {
                console.log(error);
            }
        );

        this.selectedFiles = undefined;
    }
    private initializeDays(res: IImputation) {
        res.monthlyImputations.forEach(monthly => {
            monthly.dailyImputations.forEach(daily => this.days.push(daily.day));
        });
        this.removeDuplecates(this.days);
    }

    private removeDuplecates(days: Array<number>) {
        this.days = Array.from(new Set(days)).sort((a, b) => a - b);
    }
}
