package com.lifequestmanager.service;

import com.lifequestmanager.model.Task;
import com.lifequestmanager.model.User;
import com.lifequestmanager.repository.TaskRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;
@Service
public class TaskService {

    @Autowired
    private TaskRepository taskRepository;

    public Task createTask(User user, String description, Recurrence recurrence, LocalDate startDate, LocalDate endDate) {
        Task task = new Task();
        task.setUser(user);
        task.setDescription(description);
        task.setRecurrence(recurrence);
        task.setStartDate(startDate);
        task.setEndDate(endDate);
        return taskRepository.save(task);
    }

    public List<Task> getTasksForUser(User user) {
        return taskRepository.findByUser(user);
    }

    public List<Task> getTasksForDay(User user, LocalDate date) {
        List<Task> userTasks = taskRepository.findByUser(user);
        return userTasks.stream()
                .filter(task -> isTaskForDate(task, date))
                .collect(Collectors.toList());
    }

    private boolean isTaskForDate(Task task, LocalDate date) {
        LocalDate start = task.getStartDate();
        LocalDate end = task.getEndDate();
        if (date.isBefore(start) || (end != null && date.isAfter(end))) {
            return false;
        }
        switch (task.getRecurrence()) {
            case NONE:
                return date.equals(start);
            case DAILY:
                return true;
            case WEEKLY:
                return start.getDayOfWeek().equals(date.getDayOfWeek());
            case MONTHLY:
                return start.getDayOfMonth() == date.getDayOfMonth();
            default:
                return false;
        }
    }
}
