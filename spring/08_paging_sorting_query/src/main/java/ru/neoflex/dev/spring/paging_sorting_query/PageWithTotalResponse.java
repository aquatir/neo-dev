package ru.neoflex.dev.spring.paging_sorting_query;

import java.util.List;

public class PageWithTotalResponse<T> {
    List<T> data;
    long totalElements;

    public PageWithTotalResponse() {};
    public PageWithTotalResponse(List<T> data, long totalElements) {
        this.data = data;
        this.totalElements = totalElements;
    }

    public List<T> getData() {
        return data;
    }

    public void setData(List<T> data) {
        this.data = data;
    }

    public long getTotalElements() {
        return totalElements;
    }

    public void setTotalElements(long totalElements) {
        this.totalElements = totalElements;
    }

    @Override
    public String toString() {
        return "PageWithTotalResponse{" +
                "data=" + data +
                ", totalElements=" + totalElements +
                '}';
    }
}
