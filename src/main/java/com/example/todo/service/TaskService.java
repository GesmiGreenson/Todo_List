package com.example.todo.service;

import java.util.List;

import com.example.todo.entity.Task;
import com.example.todo.entity.User;

public interface TaskService {
    List<Task> getAllTasksByUser(User user);
    Task getTaskById(Long id);
    Task saveTask(Task task);
    void deleteTask(Long id);
    List<Task> getTasksByCompletionStatus(User user, boolean completed);
}