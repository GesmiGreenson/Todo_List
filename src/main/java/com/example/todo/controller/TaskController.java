package com.example.todo.controller;

import com.example.todo.dto.TaskDto;
import com.example.todo.entity.Task;
import com.example.todo.entity.User;
import com.example.todo.service.TaskService;
import com.example.todo.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.validation.Valid;
import java.util.List;

@Controller
@RequestMapping("/dashboard")
public class TaskController {

    @Autowired
    private TaskService taskService;

    @Autowired
    private UserService userService;

    private User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        return userService.findByEmail(email);
    }

    @GetMapping
    public String dashboard(Model model, @RequestParam(value = "filter", required = false) String filter) {
        try {
            User user = getCurrentUser();
            List<Task> tasks;
            
            if ("completed".equals(filter)) {
                tasks = taskService.getTasksByCompletionStatus(user, true);
            } else if ("pending".equals(filter)) {
                tasks = taskService.getTasksByCompletionStatus(user, false);
            } else {
                tasks = taskService.getAllTasksByUser(user);
            }
            
            model.addAttribute("tasks", tasks);
            model.addAttribute("task", new TaskDto());
            model.addAttribute("user", user);
            return "dashboard";
        } catch (Exception e) {
            e.printStackTrace(); // Add this to see the actual error
            return "redirect:/error";
        }
    }

    @PostMapping("/tasks")
    public String createTask(@ModelAttribute("task") @Valid TaskDto taskDto, 
                           BindingResult result, 
                           Model model,
                           RedirectAttributes redirectAttributes) {
        
        if (result.hasErrors()) {
            User user = getCurrentUser();
            model.addAttribute("tasks", taskService.getAllTasksByUser(user));
            model.addAttribute("user", user);
            return "dashboard";
        }

        try {
            User user = getCurrentUser();
            Task task = new Task(taskDto.getTitle(), taskDto.getDescription(), user);
            taskService.saveTask(task);
            redirectAttributes.addFlashAttribute("success", "Task created successfully!");
            return "redirect:/dashboard";
        } catch (Exception e) {
            e.printStackTrace(); // Add this to see the actual error
            redirectAttributes.addFlashAttribute("error", "Failed to create task. Please try again.");
            return "redirect:/dashboard";
        }
    }
    @PostMapping("/tasks/{id}/toggle")
    public String toggleTaskCompletion(@PathVariable Long id) {
        try {
            Task task = taskService.getTaskById(id);
            User currentUser = getCurrentUser();
            
            if (task != null && task.getUser().getId().equals(currentUser.getId())) {
                task.setCompleted(!task.isCompleted());
                taskService.saveTask(task);
            }
            return "redirect:/dashboard";
        } catch (Exception e) {
            return "redirect:/dashboard?error";
        }
    }

    @GetMapping("/tasks/{id}/edit")
    public String showEditForm(@PathVariable Long id, Model model) {
        try {
            Task task = taskService.getTaskById(id);
            User currentUser = getCurrentUser();
            
            if (task == null || !task.getUser().getId().equals(currentUser.getId())) {
                return "redirect:/dashboard?error=access_denied";
            }
            
            TaskDto taskDto = new TaskDto();
            taskDto.setId(task.getId());
            taskDto.setTitle(task.getTitle());
            taskDto.setDescription(task.getDescription());
            taskDto.setCompleted(task.isCompleted());
            
            model.addAttribute("task", taskDto);
            return "edit-task";
        } catch (Exception e) {
            return "redirect:/dashboard?error";
        }
    }

    @PostMapping("/tasks/{id}/edit")
    public String updateTask(@PathVariable Long id, @ModelAttribute("task") @Valid TaskDto taskDto,
                           BindingResult result) {
        if (result.hasErrors()) {
            return "edit-task";
        }

        try {
            Task task = taskService.getTaskById(id);
            User currentUser = getCurrentUser();
            
            if (task != null && task.getUser().getId().equals(currentUser.getId())) {
                task.setTitle(taskDto.getTitle());
                task.setDescription(taskDto.getDescription());
                taskService.saveTask(task);
            }
            return "redirect:/dashboard?success";
        } catch (Exception e) {
            return "redirect:/dashboard?error";
        }
    }

    @PostMapping("/tasks/{id}/delete")
    public String deleteTask(@PathVariable Long id) {
        try {
            Task task = taskService.getTaskById(id);
            User currentUser = getCurrentUser();
            
            if (task != null && task.getUser().getId().equals(currentUser.getId())) {
                taskService.deleteTask(id);
            }
            return "redirect:/dashboard?success";
        } catch (Exception e) {
            return "redirect:/dashboard?error";
        }
    }
}