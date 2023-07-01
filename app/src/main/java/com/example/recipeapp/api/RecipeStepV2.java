package com.example.recipeapp.api;

public class RecipeStepV2 {
    private int step;
    private String instructions;

    public RecipeStepV2(int step, String instructions) {
        this.step = step;
        this.instructions = instructions;
    }

    public int getStep() {
        return step;
    }

    public void setStep(int step) {
        this.step = step;
    }

    public String getInstructions() {
        return instructions;
    }

    public void setInstructions(String instructions) {
        this.instructions = instructions;
    }

    @Override
    public String toString() {
        return "RecipeStepV2{" +
                "step=" + step +
                ", instructions='" + instructions + '\'' +
                '}';
    }
}
