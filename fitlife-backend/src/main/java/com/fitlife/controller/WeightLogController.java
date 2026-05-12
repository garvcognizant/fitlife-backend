package com.fitlife.controller;

import com.fitlife.dto.WeightLogDto;
import com.fitlife.dto.WeightLogRequest;
import com.fitlife.model.User;
import com.fitlife.service.WeightLogService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/weight-logs")
@RequiredArgsConstructor
public class WeightLogController {

    private final WeightLogService weightLogService;

    @GetMapping
    public ResponseEntity<List<WeightLogDto>> getHistory(@AuthenticationPrincipal User user) {
        return ResponseEntity.ok(weightLogService.getHistory(user.getId()));
    }

    @GetMapping("/latest")
    public ResponseEntity<WeightLogDto> getLatest(@AuthenticationPrincipal User user) {
        WeightLogDto dto = weightLogService.getLatest(user.getId());
        return dto != null ? ResponseEntity.ok(dto) : ResponseEntity.noContent().build();
    }

    @PostMapping
    public ResponseEntity<WeightLogDto> log(@AuthenticationPrincipal User user,
                                            @RequestBody WeightLogRequest request) {
        LocalDate date = request.getDate() != null ? request.getDate() : LocalDate.now();
        return ResponseEntity.ok(weightLogService.logWeight(user, request.getWeightKg(), date, request.getNotes()));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@AuthenticationPrincipal User user, @PathVariable Long id) {
        weightLogService.deleteLog(user.getId(), id);
        return ResponseEntity.noContent().build();
    }
}