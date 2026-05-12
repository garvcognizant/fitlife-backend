package com.fitlife.controller;

import com.fitlife.dto.MealDto;
import com.fitlife.model.User;
import com.fitlife.service.MealService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/meals")
@RequiredArgsConstructor
public class MealController {

    private final MealService mealService;

    @GetMapping
    public ResponseEntity<List<MealDto>> getTodayMeals(@AuthenticationPrincipal User user) {
        return ResponseEntity.ok(mealService.getTodayMeals(user.getId()));
    }

    @GetMapping("/all")
    public ResponseEntity<List<MealDto>> getAllMeals(@AuthenticationPrincipal User user) {
        return ResponseEntity.ok(mealService.getUserMeals(user.getId()));
    }

    @GetMapping("/history")
    public ResponseEntity<List<MealDto>> getHistory(@AuthenticationPrincipal User user,
                                                     @RequestParam String start,
                                                     @RequestParam String end) {
        return ResponseEntity.ok(mealService.getMealsByDateRange(
                user.getId(), LocalDate.parse(start), LocalDate.parse(end)));
    }

    @PostMapping
    public ResponseEntity<MealDto> createMeal(@AuthenticationPrincipal User user,
                                               @RequestBody MealDto dto) {
        return ResponseEntity.ok(mealService.createMeal(user, dto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<MealDto> updateMeal(@AuthenticationPrincipal User user,
                                               @PathVariable Long id,
                                               @RequestBody MealDto dto) {
        return ResponseEntity.ok(mealService.updateMeal(user.getId(), id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteMeal(@AuthenticationPrincipal User user,
                                            @PathVariable Long id) {
        mealService.deleteMeal(user.getId(), id);
        return ResponseEntity.noContent().build();
    }
}
