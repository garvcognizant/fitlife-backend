package com.fitlife.service;

import com.fitlife.dto.WeightLogDto;
import com.fitlife.exception.BadRequestException;
import com.fitlife.exception.ForbiddenException;
import com.fitlife.exception.NotFoundException;
import com.fitlife.model.User;
import com.fitlife.model.WeightLog;
import com.fitlife.repository.WeightLogRepository;
import com.fitlife.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class WeightLogService {

    private final WeightLogRepository weightLogRepository;
    private final UserRepository userRepository;

    public List<WeightLogDto> getHistory(Long userId) {
        return weightLogRepository.findByUserIdOrderByDateDesc(userId)
                .stream().map(WeightLogDto::from).toList();
    }

    public WeightLogDto getLatest(Long userId) {
        return weightLogRepository.findFirstByUserIdOrderByDateDesc(userId)
                .map(WeightLogDto::from).orElse(null);
    }

    @Transactional
    public WeightLogDto logWeight(User user, Double weightKg, LocalDate date, String notes) {
        if (weightKg == null || weightKg <= 0) throw new BadRequestException("Weight must be positive");
        WeightLog log = WeightLog.builder()
                .user(user)
                .weightKg(weightKg)
                .date(date != null ? date : LocalDate.now())
                .notes(notes)
                .build();
        log = weightLogRepository.save(log);
        // Sync User.weightKg with latest log for BMR/TDEE calculations
        user.setWeightKg(weightKg);
        userRepository.save(user);
        return WeightLogDto.from(log);
    }

    @Transactional
    public void deleteLog(Long userId, Long logId) {
        WeightLog log = weightLogRepository.findById(logId)
                .orElseThrow(() -> new NotFoundException("Weight log not found"));
        if (!log.getUser().getId().equals(userId)) {
            throw new ForbiddenException("Access denied to this weight log");
        }
        weightLogRepository.delete(log);
    }
}
