package com.example.todo.repository;
import com.example.todo.entity.Task;
import com.example.todo.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface TaskRepository extends JpaRepository<Task, Long> {
    List<Task> findByUserOrderByCreatedAtDesc(User user);
    List<Task> findByUserAndCompletedOrderByCreatedAtDesc(User user, boolean completed);
}