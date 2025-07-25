package com.lifequestmanager.controller;

import com.lifequestmanager.model.Task;
import com.lifequestmanager.model.User;
import com.lifequestmanager.service.TaskService;
import com.lifequestmanager.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/tasks")
public class TaskController {

    @Autowired
    private TaskService taskService;

    @Autowired
    private UserService userService;

    @PostMapping
    public ResponseEntity<Task> createTask(
            @RequestParam String description,
            @RequestParam Recurrence recurrence,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            Authentication authentication) {
        String username = authentication.getName();
        User user = userService.findByUsername(username).orElseThrow();
        Task task = taskService.createTask(user, description, recurrence, startDate, endDate);
        return ResponseEntity.ok(task);
    }

    @GetMapping
    public ResponseEntity<List<Task>> getTasks(Authentication authentication) {
        String username = authentication.getName();
        User user = userService.findByUsername(username).orElseThrow();
        List<Task> tasks = taskService.getTasksForUser(user);
        return ResponseEntity.ok(tasks);
    }

    @GetMapping("/daily")
    public ResponseEntity<List<Task>> getTasksForDay(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            Authentication authentication) {
        String username = authentication.getName();
        User user = userService.findByUsername(username).orElseThrow();
        List<Task> tasks = taskService.getTasksForDay(user, date);
        return ResponseEntity.ok(tasks);
    }
}
