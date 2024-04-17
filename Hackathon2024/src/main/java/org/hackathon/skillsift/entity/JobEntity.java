package org.hackathon.skillsift.entity;

import javax.persistence.*;

@Entity(name = "job")
public class JobEntity {

    @Id
    private Integer jobId;

    private String jobDescription;

    public Integer getJobId() {
        return jobId;
    }

    public void setJobId(Integer jobId) {
        this.jobId = jobId;
    }

    public String getJobDescription() {
        return jobDescription;
    }

    public void setJobDescription(String jobDescription) {
        this.jobDescription = jobDescription;
    }
}
