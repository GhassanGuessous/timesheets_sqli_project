import { Injectable } from '@angular/core';
import { NgbModal } from '@ng-bootstrap/ng-bootstrap';

import { AuthTbpModalComponent } from 'app/shared/authTbp/auth-tbp.component';

@Injectable({ providedIn: 'root' })
export class AuthTbpModalService {
    private isOpen = false;
    constructor(private modalService: NgbModal) {}

    open(requestBody: any) {
        return new Promise((resolve, reject) => {
            if (this.isOpen) {
                return;
            }
            this.isOpen = true;
            const modalRef = this.modalService.open(AuthTbpModalComponent);
            (<AuthTbpModalComponent>modalRef.componentInstance).requestBody = requestBody;

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
