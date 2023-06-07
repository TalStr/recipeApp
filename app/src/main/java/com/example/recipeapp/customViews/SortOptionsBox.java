package com.example.recipeapp.customViews;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ToggleButton;

import androidx.core.content.ContextCompat;

import com.example.recipeapp.R;
import com.example.recipeapp.databinding.SortOptionsBoxBinding;

public class SortOptionsBox extends LinearLayout {
    public Button selectedButton;
    public boolean state; // true = ascending, false = descending
    private SortOptionsBoxBinding binding;
    private SortListener listener;
    public interface SortListener {
        void onSortSelected(String by, boolean state);
    }
    public void setSortListener(SortListener listener) {
        this.listener = listener;
    }


    public SortOptionsBox(Context context) {
        super(context);
        initialize(context);
    }
    public SortOptionsBox(Context context, AttributeSet attrs) {
        super(context, attrs);
        initialize(context);
    }

    private void initialize(Context context) {
        binding = SortOptionsBoxBinding.inflate(LayoutInflater.from(context), this, true);
        selectedButton = binding.rating;
        state = true;

        // Set the initial button background
        selectedButton.setBackgroundColor(ContextCompat.getColor(getContext(), android.R.color.holo_blue_dark));
        selectedButton.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.baseline_arrow_downward_24, 0);
        binding.time.setBackgroundColor(ContextCompat.getColor(getContext(), android.R.color.darker_gray));
        binding.price.setBackgroundColor(ContextCompat.getColor(getContext(), android.R.color.darker_gray));
        // Setup onClickListeners
        binding.rating.setOnClickListener(v -> selectButton(binding.rating));
        binding.time.setOnClickListener(v -> selectButton(binding.time));
        binding.price.setOnClickListener(v -> selectButton(binding.price));
    }

    private void selectButton(Button button){
        if(selectedButton == button) {
            // If the selected button is clicked again, just switch the sorting order
            state = !state;
        } else {
            // Change the background color of the previously selected button
            selectedButton.setBackgroundColor(ContextCompat.getColor(getContext(), android.R.color.darker_gray));
            selectedButton.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
            // Update the selected button
            selectedButton = button;
            // Change the background color of the currently selected button
            selectedButton.setBackgroundColor(ContextCompat.getColor(getContext(), android.R.color.holo_blue_dark));
            // Reset the sorting order to default when a new button is selected
            state = true;
        }

        // Change the arrow direction based on the sorting order
        if(state) {
            selectedButton.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.baseline_arrow_downward_24, 0);
        } else {
            selectedButton.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.baseline_arrow_upward_24, 0);
        }
        if (listener != null) {
            listener.onSortSelected(button.getText().toString(), state);
        }
    }
}
