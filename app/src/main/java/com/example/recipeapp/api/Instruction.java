package com.example.recipeapp.api;

import java.io.Serializable;

public class Instruction implements Serializable {
    public int recipe_id;
    public int step;
    public String description;
}
