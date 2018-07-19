package com.gracelogic.platform.db.dto;

import java.util.LinkedList;

public class EntityListResponse<E extends IdObjectDTO> {
    private Integer pages = 0;
    private Integer page = 1;
    private Integer totalCount = 0;
    private Integer queriedCount = 0;
    private Integer startRecord;

    public EntityListResponse(Integer totalCount, Integer countPerPage, Integer page, Integer start) {
        this.queriedCount = countPerPage;
        this.totalCount = totalCount;

        if (totalCount != null) {
            this.pages = ((totalCount / countPerPage));
            if (totalCount % 2 != 0) {
                pages++;
            }
        }

        if (page != null) {
            this.page = page;
            this.startRecord = (page * countPerPage) - countPerPage;
        }
        else {
            if (countPerPage != null && start != null) {
                if (start > countPerPage) {
                    this.page = start / countPerPage;
                }
            }
            this.startRecord = start;
        }
    }

    public EntityListResponse() {
    }

    private LinkedList<E> data = new LinkedList<E>();

    public Integer getPages() {
        return pages;
    }

    public void setPages(Integer pages) {
        this.pages = pages;
    }

    public Integer getPage() {
        return page;
    }

    public void setPage(Integer page) {
        this.page = page;
    }
    public LinkedList<E> getData() {
        return data;
    }

    public void setData(LinkedList<E> data) {
        this.data = data;
    }

    public void addData(E idObjectModel) {
        data.addLast(idObjectModel);
    }

    public Integer getTotalCount() {
        return totalCount;
    }

    public void setTotalCount(Integer totalCount) {
        this.totalCount = totalCount;
    }

    public Integer getQueriedCount() {
        return queriedCount;
    }

    public void setQueriedCount(Integer queriedCount) {
        this.queriedCount = queriedCount;
    }

    //For datatables
    public int getRecordsTotal() {
        return getTotalCount();
    }

    public int getRecordsFiltered() {
        return getTotalCount();
    }

    public Integer getStartRecord() {
        return startRecord;
    }

    public void setStartRecord(Integer startRecord) {
        this.startRecord = startRecord;
    }
}
