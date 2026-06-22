package com.codesolutions.pmt.repository;

import com.codesolutions.pmt.domain.Project;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProjectRepository extends JpaRepository<Project, Long> {
}
