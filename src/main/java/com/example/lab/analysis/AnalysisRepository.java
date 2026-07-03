package com.example.lab.analysis;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AnalysisRepository extends JpaRepository<Analysis, Long> {

	List<Analysis> findAllByUserId(Long userId);
}
