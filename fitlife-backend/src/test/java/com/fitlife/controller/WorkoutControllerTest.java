package com.fitlife.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fitlife.dto.WorkoutDto;
import com.fitlife.model.User;
import com.fitlife.service.WorkoutService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(WorkoutController.class)
@AutoConfigureMockMvc(addFilters = false) // disable security filters for unit testing
class WorkoutControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private WorkoutService workoutService;

    // Mock security dependencies so @WebMvcTest can load context
    @MockitoBean
    private com.fitlife.security.JwtAuthenticationFilter jwtAuthenticationFilter;

    @MockitoBean
    private org.springframework.web.cors.CorsConfigurationSource corsConfigurationSource;

    private User testUser;
    private WorkoutDto sampleWorkout;

    @BeforeEach
    void setUp() {
        testUser = User.builder().id(1L).fullName("Test User").email("test@test.com").password("pass").build();

        sampleWorkout = new WorkoutDto();
        sampleWorkout.setId(1L);
        sampleWorkout.setExerciseName("Bench Press");
        sampleWorkout.setExerciseType("STRENGTH");
        sampleWorkout.setSets(3);
        sampleWorkout.setReps(10);
        sampleWorkout.setWeightLbs(135.0);
        sampleWorkout.setCaloriesBurned(200);
        sampleWorkout.setDate(LocalDateTime.now());

        // Set authenticated user in security context
        var auth = new UsernamePasswordAuthenticationToken(testUser, null, Collections.emptyList());
        SecurityContextHolder.getContext().setAuthentication(auth);
    }

    @Test
    void getTodayWorkouts_shouldReturnOk() throws Exception {
        when(workoutService.getTodayWorkouts(1L)).thenReturn(List.of(sampleWorkout));

        mockMvc.perform(get("/api/workouts")
                        .principal(new UsernamePasswordAuthenticationToken(testUser, null, Collections.emptyList())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].exerciseName").value("Bench Press"));
    }

    @Test
    void getAllWorkouts_shouldReturnOk() throws Exception {
        when(workoutService.getUserWorkouts(1L)).thenReturn(List.of(sampleWorkout));

        mockMvc.perform(get("/api/workouts/all")
                        .principal(new UsernamePasswordAuthenticationToken(testUser, null, Collections.emptyList())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1));
    }

    @Test
    void createWorkout_shouldReturnOk() throws Exception {
        when(workoutService.createWorkout(any(User.class), any(WorkoutDto.class))).thenReturn(sampleWorkout);

        mockMvc.perform(post("/api/workouts")
                        .principal(new UsernamePasswordAuthenticationToken(testUser, null, Collections.emptyList()))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(sampleWorkout)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.exerciseName").value("Bench Press"));
    }

    @Test
    void deleteWorkout_shouldReturnNoContent() throws Exception {
        mockMvc.perform(delete("/api/workouts/1")
                        .principal(new UsernamePasswordAuthenticationToken(testUser, null, Collections.emptyList())))
                .andExpect(status().isNoContent());
    }

    @Test
    void getHistory_shouldReturnOk() throws Exception {
        when(workoutService.getWorkoutsByDateRange(eq(1L), any(), any())).thenReturn(List.of(sampleWorkout));

        mockMvc.perform(get("/api/workouts/history")
                        .param("start", "2026-05-01")
                        .param("end", "2026-05-20")
                        .principal(new UsernamePasswordAuthenticationToken(testUser, null, Collections.emptyList())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].exerciseType").value("STRENGTH"));
    }
}

