package org.hackathon.skillsift.repository;

import org.hackathon.skillsift.entity.JobEntity;
import org.hackathon.skillsift.entity.ResumeEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ResumeRepository extends JpaRepository<ResumeEntity, Integer> {

    List<ResumeEntity> findByJob(JobEntity jobId);
}
