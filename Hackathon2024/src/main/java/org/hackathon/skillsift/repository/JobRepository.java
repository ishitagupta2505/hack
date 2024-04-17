package org.hackathon.skillsift.repository;

import org.hackathon.skillsift.entity.JobEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface JobRepository extends JpaRepository<JobEntity, Integer> {
}
