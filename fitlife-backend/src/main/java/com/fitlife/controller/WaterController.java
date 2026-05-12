package com.fitlife.controller;

import com.fitlife.dto.WaterLogDto;
import com.fitlife.model.User;
import com.fitlife.service.WaterService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/water")
@RequiredArgsConstructor
public class WaterController {

    private final WaterService waterService;

    @GetMapping
    public ResponseEntity<List<WaterLogDto>> getToday(@AuthenticationPrincipal User user) {
        return ResponseEntity.ok(waterService.getTodayLogs(user.getId()));
    }

    @GetMapping("/all")
    public ResponseEntity<List<WaterLogDto>> getAll(@AuthenticationPrincipal User user) {
        return ResponseEntity.ok(waterService.getAllLogs(user.getId()));
    }

    @GetMapping("/history")
    public ResponseEntity<List<WaterLogDto>> getHistory(@AuthenticationPrincipal User user,
                                                         @RequestParam String start,
                                                         @RequestParam String end) {
        return ResponseEntity.ok(waterService.getLogsByDateRange(
                user.getId(), LocalDate.parse(start), LocalDate.parse(end)));
    }

    @PostMapping
    public ResponseEntity<WaterLogDto> add(@AuthenticationPrincipal User user,
                                           @RequestBody Map<String, Integer> body) {
        int amount = body.getOrDefault("amountMl", 0);
        return ResponseEntity.ok(waterService.addLog(user, amount));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@AuthenticationPrincipal User user, @PathVariable Long id) {
        waterService.deleteLog(user.getId(), id);
        return ResponseEntity.noContent().build();
    }
}
