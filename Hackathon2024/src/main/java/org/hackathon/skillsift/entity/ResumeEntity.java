package org.hackathon.skillsift.entity;

import javax.persistence.*;

@Entity(name = "resume")
public class ResumeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer resumeId;

    private String name;

    private String technologies;

    private String college;

    private Integer yearsOfExperience;

    private String fileName;

    @Lob
    private byte[] fileData;

    @ManyToOne
    @JoinColumn(name = "job_id", referencedColumnName = "jobId")
    private JobEntity job;

    public Integer getResumeId() {
        return resumeId;
    }

    public void setResumeId(Integer resumeId) {
        this.resumeId = resumeId;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public byte[] getFileData() {
        return fileData;
    }

    public void setFileData(byte[] fileData) {
        this.fileData = fileData;
    }

    public JobEntity getJob() {
        return job;
    }

    public void setJob(JobEntity job) {
        this.job = job;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTechnologies() {
        return technologies;
    }

    public void setTechnologies(String technologies) {
        this.technologies = technologies;
    }

    public String getCollege() {
        return college;
    }

    public void setCollege(String college) {
        this.college = college;
    }

    public Integer getYearsOfExperience() {
        return yearsOfExperience;
    }

    public void setYearsOfExperience(Integer yearsOfExperience) {
        this.yearsOfExperience = yearsOfExperience;
    }
}