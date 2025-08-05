package com.capstone.backend.dto;

import com.capstone.backend.domain.Food;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.ArrayList;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ResolvedMealResponse {
    private List<Food> breakfast;
    private List<Food> lunch;
    private List<Food> dinner;

    public List<Food> getAllFoods() {
        List<Food> all = new ArrayList<>();
        if (breakfast != null) all.addAll(breakfast);
        if (lunch != null) all.addAll(lunch);
        if (dinner != null) all.addAll(dinner);
        return all;
    }

}
