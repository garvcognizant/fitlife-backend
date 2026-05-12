package com.fitlife.service;

import com.fitlife.dto.GoalDto;
import com.fitlife.exception.ForbiddenException;
import com.fitlife.exception.NotFoundException;
import com.fitlife.model.Goal;
import com.fitlife.model.User;
import com.fitlife.repository.GoalRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class GoalService {

    private final GoalRepository goalRepository;

    public List<GoalDto> getUserGoals(Long userId) {
        return goalRepository.findByUserIdOrderByCreatedAtDesc(userId)
                .stream().map(GoalDto::from).toList();
    }

    @Transactional
    public GoalDto createGoal(User user, GoalDto dto) {
        Goal goal = Goal.builder()
                .user(user)
                .title(dto.getTitle())
                .type(dto.getType())
                .targetValue(dto.getTargetValue())
                .currentValue(dto.getCurrentValue() != null ? dto.getCurrentValue() : 0.0)
                .unit(dto.getUnit())
                .deadline(dto.getDeadline())
                .build();
        return GoalDto.from(goalRepository.save(goal));
    }

    @Transactional
    public GoalDto updateGoal(Long userId, Long goalId, GoalDto dto) {
        Goal goal = goalRepository.findById(goalId)
                .orElseThrow(() -> new NotFoundException("Goal not found"));
        if (!goal.getUser().getId().equals(userId)) {
            throw new ForbiddenException("Access denied to this goal");
        }

        if (dto.getTitle() != null) goal.setTitle(dto.getTitle());
        if (dto.getTargetValue() != null) goal.setTargetValue(dto.getTargetValue());
        if (dto.getCurrentValue() != null) goal.setCurrentValue(dto.getCurrentValue());
        if (dto.getUnit() != null) goal.setUnit(dto.getUnit());
        if (dto.getDeadline() != null) goal.setDeadline(dto.getDeadline());
        if (dto.getCompleted() != null) goal.setCompleted(dto.getCompleted());

        // Auto-complete when current >= target (with null safety)
        if (goal.getCurrentValue() != null && goal.getTargetValue() != null
                && goal.getCurrentValue() >= goal.getTargetValue()) {
            goal.setCompleted(true);
        }

        return GoalDto.from(goalRepository.save(goal));
    }

    @Transactional
    public void deleteGoal(Long userId, Long goalId) {
        Goal goal = goalRepository.findById(goalId)
                .orElseThrow(() -> new NotFoundException("Goal not found"));
        if (!goal.getUser().getId().equals(userId)) {
            throw new ForbiddenException("Access denied to this goal");
        }
        goalRepository.delete(goal);
    }
}
