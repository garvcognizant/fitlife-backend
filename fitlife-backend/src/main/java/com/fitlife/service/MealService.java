package com.fitlife.service;

import com.fitlife.dto.MealDto;
import com.fitlife.exception.ForbiddenException;
import com.fitlife.exception.NotFoundException;
import com.fitlife.model.Meal;
import com.fitlife.model.User;
import com.fitlife.repository.MealRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class MealService {

    private final MealRepository mealRepository;

    public List<MealDto> getTodayMeals(Long userId) {
        LocalDateTime startOfDay = LocalDate.now().atStartOfDay();
        LocalDateTime endOfDay = LocalDate.now().atTime(LocalTime.MAX);
        return mealRepository.findByUserIdAndDateBetween(userId, startOfDay, endOfDay)
                .stream().map(MealDto::from).toList();
    }

    public List<MealDto> getUserMeals(Long userId) {
        return mealRepository.findByUserIdOrderByDateDesc(userId)
                .stream().map(MealDto::from).toList();
    }

    public List<MealDto> getMealsByDateRange(Long userId, LocalDate start, LocalDate end) {
        return mealRepository.findByUserIdAndDateBetween(userId,
                start.atStartOfDay(), end.atTime(LocalTime.MAX))
                .stream().map(MealDto::from).toList();
    }

    @Transactional
    public MealDto createMeal(User user, MealDto dto) {
        Meal meal = Meal.builder()
                .user(user)
                .foodName(dto.getFoodName())
                .mealType(dto.getMealType())
                .quantity(dto.getQuantity())
                .unit(dto.getUnit())
                .calories(dto.getCalories())
                .protein(dto.getProtein())
                .carbs(dto.getCarbs())
                .fat(dto.getFat())
                .build();
        return MealDto.from(mealRepository.save(meal));
    }

    @Transactional
    public MealDto updateMeal(Long userId, Long mealId, MealDto dto) {
        Meal meal = mealRepository.findById(mealId)
                .orElseThrow(() -> new NotFoundException("Meal not found"));
        if (!meal.getUser().getId().equals(userId)) {
            throw new ForbiddenException("Access denied to this meal");
        }
        meal.setFoodName(dto.getFoodName());
        meal.setMealType(dto.getMealType());
        meal.setQuantity(dto.getQuantity());
        meal.setUnit(dto.getUnit());
        meal.setCalories(dto.getCalories());
        meal.setProtein(dto.getProtein());
        meal.setCarbs(dto.getCarbs());
        meal.setFat(dto.getFat());
        return MealDto.from(mealRepository.save(meal));
    }

    @Transactional
    public void deleteMeal(Long userId, Long mealId) {
        Meal meal = mealRepository.findById(mealId)
                .orElseThrow(() -> new NotFoundException("Meal not found"));
        if (!meal.getUser().getId().equals(userId)) {
            throw new ForbiddenException("Access denied to this meal");
        }
        mealRepository.delete(meal);
    }
}
