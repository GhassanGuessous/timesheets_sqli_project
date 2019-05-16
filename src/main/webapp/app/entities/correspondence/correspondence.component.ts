import { Component, OnInit, OnDestroy } from '@angular/core';
import { HttpErrorResponse, HttpHeaders, HttpResponse } from '@angular/common/http';
import { ActivatedRoute, Router } from '@angular/router';
import { Subscription } from 'rxjs';
import { filter, map } from 'rxjs/operators';
import { JhiEventManager, JhiParseLinks, JhiAlertService } from 'ng-jhipster';

import { ICorrespondence } from 'app/shared/model/correspondence.model';
import { AccountService } from 'app/core';

import { ITEMS_PER_PAGE } from 'app/shared';
import { CorrespondenceService } from './correspondence.service';

@Component({
    selector: 'jhi-correspondence',
    templateUrl: './correspondence.component.html'
})
export class CorrespondenceComponent implements OnInit, OnDestroy {
    private currentAccount: any;
    correspondences: ICorrespondence[];
    error: any;
    success: any;
    private eventSubscriber: Subscription;
    private routeData: any;
    private links: any;
    private totalItems: any;
    itemsPerPage: any;
    page: any;
    predicate: any;
    private previousPage: any;
    private reverse: any;
    private searchedKey: string;

    constructor(
        protected correspondenceService: CorrespondenceService,
        protected parseLinks: JhiParseLinks,
        protected jhiAlertService: JhiAlertService,
        protected accountService: AccountService,
        protected activatedRoute: ActivatedRoute,
        protected router: Router,
        protected eventManager: JhiEventManager
    ) {
        this.itemsPerPage = ITEMS_PER_PAGE;
        this.routeData = this.activatedRoute.data.subscribe(data => {
            this.page = data.pagingParams.page;
            this.previousPage = data.pagingParams.page;
            this.reverse = data.pagingParams.ascending;
            this.predicate = data.pagingParams.predicate;
        });
    }

    loadAll() {
        this.correspondenceService
            .query({
                page: this.page - 1,
                size: this.itemsPerPage,
                sort: this.sort()
            })
            .subscribe(
                (res: HttpResponse<ICorrespondence[]>) => this.paginateCorrespondences(res.body, res.headers),
                (res: HttpErrorResponse) => this.onError(res.message)
            );
    }

    loadPage(page: number) {
        if (page !== this.previousPage) {
            this.previousPage = page;
            this.transition();
        }
    }

    transition() {
        this.router.navigate(['/correspondence'], {
            queryParams: {
                page: this.page,
                size: this.itemsPerPage,
                sort: this.predicate + ',' + (this.reverse ? 'asc' : 'desc')
            }
        });
        this.searchedKey !== undefined ? this.findBySearchedKey() : this.loadAll();
    }

    clear() {
        this.page = 0;
        this.router.navigate([
            '/correspondence',
            {
                page: this.page,
                sort: this.predicate + ',' + (this.reverse ? 'asc' : 'desc')
            }
        ]);
        this.loadAll();
    }

    ngOnInit() {
        this.loadAll();
        this.accountService.identity().then(account => {
            this.currentAccount = account;
        });
        this.registerChangeInCorrespondences();
    }

    ngOnDestroy() {
        this.eventManager.destroy(this.eventSubscriber);
    }

    trackId(index: number, item: ICorrespondence) {
        return item.id;
    }

    registerChangeInCorrespondences() {
        this.eventSubscriber = this.eventManager.subscribe('correspondenceListModification', response => this.loadAll());
    }

    sort() {
        const result = [this.predicate + ',' + (this.reverse ? 'asc' : 'desc')];
        if (this.predicate !== 'id') {
            result.push('id');
        }
        return result;
    }

    protected paginateCorrespondences(data: ICorrespondence[], headers: HttpHeaders) {
        this.links = this.parseLinks.parse(headers.get('link'));
        this.totalItems = parseInt(headers.get('X-Total-Count'), 10);
        this.correspondences = data;
    }

    protected onError(errorMessage: string) {
        this.jhiAlertService.error(errorMessage, null, null);
    }

    findBySearchedKey() {
        if (this.searchedKey !== '') {
            this.correspondenceService
                .searchedQuery(this.searchedKey, {
                    page: this.page - 1,
                    size: this.itemsPerPage,
                    sort: this.sort()
                })
                .subscribe(
                    (res: HttpResponse<ICorrespondence[]>) => this.paginateCorrespondences(res.body, res.headers),
                    (res: HttpErrorResponse) => this.onError(res.message)
                );
        } else {
            this.loadAll();
        }
    }
}
