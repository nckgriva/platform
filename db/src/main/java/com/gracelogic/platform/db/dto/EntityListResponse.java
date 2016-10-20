package com.gracelogic.platform.db.dto;

import java.util.LinkedList;

/**
 * Author: Igor Parkhomenko
 * Date: 31.01.2015
 * Time: 14:29
 */
public class EntityListResponse<E extends IdObjectModel> {
    private Integer pages = 0;
    private Integer page = 1;
    private String entity;
    private Integer partCount = 0;
    private Integer totalCount = 0;
    private Integer queriedCount = 0;

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

    public String getEntity() {
        return entity;
    }

    public void setEntity(String entity) {
        this.entity = entity;
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

    public Integer getPartCount() {
        return partCount;
    }

    public void setPartCount(Integer partCount) {
        this.partCount = partCount;
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

}
