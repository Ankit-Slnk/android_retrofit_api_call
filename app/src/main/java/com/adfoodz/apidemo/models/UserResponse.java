package com.adfoodz.apidemo.models;

import java.util.List;

public class UserResponse {
    private int page;

    private int per_page;

    private int total;

    private int total_pages;

    private List<UserDetails> data;

    public void setPage(int page) {
        this.page = page;
    }

    public int getPage() {
        return this.page;
    }

    public void setPer_page(int per_page) {
        this.per_page = per_page;
    }

    public int getPer_page() {
        return this.per_page;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public int getTotal() {
        return this.total;
    }

    public void setTotal_pages(int total_pages) {
        this.total_pages = total_pages;
    }

    public int getTotal_pages() {
        return this.total_pages;
    }

    public void setData(List<UserDetails> data) {
        this.data = data;
    }

    public List<UserDetails> getData() {
        return this.data;
    }
}
