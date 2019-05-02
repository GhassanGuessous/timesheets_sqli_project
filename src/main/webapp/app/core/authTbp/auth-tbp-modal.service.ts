import { Injectable } from '@angular/core';
import { NgbModal } from '@ng-bootstrap/ng-bootstrap';

import { AuthTbpModalComponent } from 'app/shared/authTbp/auth-tbp.component';
import { ITbpRequestBody } from 'app/shared/model/tbp-request-body';

@Injectable({ providedIn: 'root' })
export class AuthTbpModalService {
    private isOpen = false;
    constructor(private modalService: NgbModal) {}

    open(tbpRequestBody: ITbpRequestBody) {
        return new Promise((resolve, reject) => {
            if (this.isOpen) {
                return;
            }
            this.isOpen = true;
            const modalRef = this.modalService.open(AuthTbpModalComponent);
            (<AuthTbpModalComponent>modalRef.componentInstance).tbpRequestBody = tbpRequestBody;

            modalRef.componentInstance.passEntry.subscribe(receivedEntry => {
                resolve(receivedEntry);
            });

            modalRef.result.then(
                result => {
                    this.isOpen = false;
                },
                reason => {
                    this.isOpen = false;
                }
            );
        });
    }
}
