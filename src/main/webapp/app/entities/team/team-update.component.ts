import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { HttpResponse, HttpErrorResponse } from '@angular/common/http';
import { Observable } from 'rxjs';
import { filter, map } from 'rxjs/operators';
import { JhiAlertService } from 'ng-jhipster';
import { ITeam } from 'app/shared/model/team.model';
import { TeamService } from './team.service';
import { IDeliveryCoordinator } from 'app/shared/model/delivery-coordinator.model';
import { DeliveryCoordinatorService } from 'app/entities/delivery-coordinator';
import { FormArray, FormBuilder, FormGroup, Validators } from '@angular/forms';
import { ProjectTypeService } from 'app/entities/project-type';
import { IProjectType } from 'app/shared/model/project-type.model';

@Component({
    selector: 'jhi-team-update',
    templateUrl: './team-update.component.html'
})
export class TeamUpdateComponent implements OnInit {
    team: ITeam;
    isSaving: boolean;
    deliverycoordinators: IDeliveryCoordinator[];
    projectTypes: IProjectType[];

    public editForm: FormGroup;
    public appTbpIdentifiersList: FormArray;

    constructor(
        protected jhiAlertService: JhiAlertService,
        protected teamService: TeamService,
        protected deliveryCoordinatorService: DeliveryCoordinatorService,
        protected activatedRoute: ActivatedRoute,
        private formBuilder: FormBuilder,
        protected projectTypeService: ProjectTypeService
    ) {}

    ngOnInit() {
        this.initAppTbpIdentifiersForm([this.createAppTbpIdentifier()]);
        this.isSaving = false;
        this.activatedRoute.data.subscribe(({ team }) => {
            this.team = team;
            if (this.team.id !== undefined) {
                this.fillAppTbpIdentifiersFormGroup();
            }
        });
        this.deliveryCoordinatorService
            .query({ filter: 'team-is-null' })
            .pipe(
                filter((mayBeOk: HttpResponse<IDeliveryCoordinator[]>) => mayBeOk.ok),
                map((response: HttpResponse<IDeliveryCoordinator[]>) => response.body)
            )
            .subscribe(
                (res: IDeliveryCoordinator[]) => {
                    if (!this.team.deliveryCoordinator || !this.team.deliveryCoordinator.id) {
                        this.deliverycoordinators = res;
                    } else {
                        this.deliveryCoordinatorService
                            .find(this.team.deliveryCoordinator.id)
                            .pipe(
                                filter((subResMayBeOk: HttpResponse<IDeliveryCoordinator>) => subResMayBeOk.ok),
                                map((subResponse: HttpResponse<IDeliveryCoordinator>) => subResponse.body)
                            )
                            .subscribe(
                                (subRes: IDeliveryCoordinator) => (this.deliverycoordinators = [subRes].concat(res)),
                                (subRes: HttpErrorResponse) => this.onError(subRes.message)
                            );
                    }
                },
                (res: HttpErrorResponse) => this.onError(res.message)
            );
        this.projectTypeService.findAll().subscribe(projectTypes => {
            this.projectTypes = projectTypes.body;
        });
    }

    private fillAppTbpIdentifiersFormGroup() {
        if (this.team.appTbpIdentifiers.length !== 0) {
            this.initMultipleAppTbpIdentifiersForm(this.team.appTbpIdentifiers.length);
        }
        this.fromTeamToForm();
    }

    initMultipleAppTbpIdentifiersForm(numberOfIdentifiers: number) {
        const identifiersFormGroup = this.createMultipleAppTbpIdentifiers(numberOfIdentifiers);
        this.initAppTbpIdentifiersForm(identifiersFormGroup);
    }

    private createMultipleAppTbpIdentifiers(numberOfIdentifiers: number) {
        const identifiersFormGroup = [];
        for (let i = 0; i < numberOfIdentifiers; i++) {
            identifiersFormGroup.push(this.createAppTbpIdentifier());
        }
        return identifiersFormGroup;
    }

    initAppTbpIdentifiersForm(formGroupArray) {
        this.editForm = this.formBuilder.group({
            id: [''],
            name: [''],
            displayName: [''],
            deliveryCoordinator: [''],
            projectType: [''],
            appTbpIdentifiers: this.formBuilder.array(formGroupArray)
        });

        this.appTbpIdentifiersList = this.editForm.get('appTbpIdentifiers') as FormArray;
    }

    private fromTeamToForm() {
        this.editForm.setValue({
            id: this.team.id,
            name: this.team.name,
            displayName: this.team.displayName,
            projectType: this.team.projectType,
            deliveryCoordinator: this.team.deliveryCoordinator,
            appTbpIdentifiers:
                this.team.appTbpIdentifiers === undefined
                    ? this.formBuilder.array([this.createAppTbpIdentifier()]).value
                    : this.formBuilder.array(this.team.appTbpIdentifiers).value
        });
    }

    get appTbpIdentifiersFormGroup() {
        return this.editForm.get('appTbpIdentifiers') as FormArray;
    }

    previousState() {
        window.history.back();
    }

    save() {
        this.team = this.editForm.value;
        console.log(this.team);
        this.isSaving = true;
        if (this.team.id) {
            this.subscribeToSaveResponse(this.teamService.update(this.team));
        } else {
            this.subscribeToSaveResponse(this.teamService.create(this.team));
        }
    }

    protected subscribeToSaveResponse(result: Observable<HttpResponse<ITeam>>) {
        result.subscribe((res: HttpResponse<ITeam>) => this.onSaveSuccess(), (res: HttpErrorResponse) => this.onSaveError());
    }

    protected onSaveSuccess() {
        this.isSaving = false;
        this.previousState();
    }

    protected onSaveError() {
        this.isSaving = false;
    }

    protected onError(errorMessage: string) {
        this.jhiAlertService.error(errorMessage, null, null);
    }

    trackDeliveryCoordinatorById(index: number, item: IDeliveryCoordinator) {
        return item.id;
    }

    createAppTbpIdentifier(): FormGroup {
        return this.formBuilder.group({
            id: [''],
            mission: ['', Validators.compose([Validators.required])],
            agresso: ['', Validators.compose([Validators.required])],
            idTbp: ['', Validators.compose([Validators.required])]
        });
    }

    addAppTbpIdentifier() {
        this.appTbpIdentifiersList.push(this.createAppTbpIdentifier());
    }

    removeAppTbpIdentifier(index) {
        this.appTbpIdentifiersList.removeAt(index);
    }

    getAppTbpIdentifiersFormGroup(index): FormGroup {
        const formGroup = this.appTbpIdentifiersList.controls[index] as FormGroup;
        return formGroup;
    }
}
