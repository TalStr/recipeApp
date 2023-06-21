package com.example.recipeapp.api;

import java.util.List;

public class FilterOptions {
    public List<Integer> priceFilter;
    // Time Range
    public int minTime;
    public int maxTime;
    public List<String> excludeIngredients;
    public List<String> includeIngredients;

    public FilterOptions(List<Integer> priceFilter, int minTime, int maxTime,
                         List<String> excludeIngredients, List<String> includeIngredients) {
        this.priceFilter = priceFilter;
        this.minTime = minTime;
        this.maxTime = maxTime;
        this.excludeIngredients = excludeIngredients;
        this.includeIngredients = includeIngredients;
    }

    @Override
    public String toString() {
        return "FilterOptions{" +
                "priceFilter=" + priceFilter +
                ", minTime=" + minTime +
                ", maxTime=" + maxTime +
                ", exludeIngredients=" + excludeIngredients +
                ", includeIngredients=" + includeIngredients +
                '}';
    }
}
