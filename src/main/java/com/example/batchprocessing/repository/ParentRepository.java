package com.example.batchprocessing.repository;

import com.example.batchprocessing.model.Parent;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ParentRepository extends JpaRepository<Parent, Long> {
}
