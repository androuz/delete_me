package com.example.batchprocessing.repository;

import com.example.batchprocessing.model.Child;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ChildRepository extends JpaRepository<Child, Long> {
}
