package org.hackathon.skillsift.controller;

import org.hackathon.skillsift.entity.JobEntity;
import org.hackathon.skillsift.entity.ResumeEntity;
import org.hackathon.skillsift.repository.JobRepository;
import org.hackathon.skillsift.repository.ResumeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
public class ResumeController {

    @Autowired
    JobRepository jobRepository;

    @Autowired
    ResumeRepository resumeRepository;

    @GetMapping("/resumes")
    public List<ResumeEntity> getResumes() {
        return resumeRepository.findAll();
    }

    @RequestMapping(value = "/resumes/{jobId}", method = RequestMethod.GET)
    public List<ResumeEntity> getResumesWithJobId(@PathVariable("jobId") Integer jobId) {
        Optional<JobEntity> job = jobRepository.findById(jobId);

        if (job.isPresent()) {
            List<ResumeEntity> resumeEntityOptional = resumeRepository.findByJob(job.get());
            return resumeEntityOptional.stream().toList();
        }

        return null;
    }

}