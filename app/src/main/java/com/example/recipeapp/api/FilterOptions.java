package com.example.recipeapp.api;

import java.util.List;

public class FilterOptions {
    // 0=rating, 1=price, 2=Time
    public int orderBy;
    // What Prices to use
    public List<Integer> priceFilter;
    // Time Range
    public int minTime;
    public int maxTime;

    public FilterOptions(int orderBy, List<Integer> priceFilter, int minTime, int maxTime) {
        this.orderBy = orderBy;
        this.priceFilter = priceFilter;
        this.minTime = minTime;
        this.maxTime = maxTime;
    }

    @Override
    public String toString() {
        return "FilterOptions{" +
                "orderBy=" + orderBy +
                ", priceFilter=" + priceFilter +
                ", minTime=" + minTime +
                ", maxTime=" + maxTime +
                '}';
    }
}
