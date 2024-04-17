package org.hackathon.skillsift.service;

import org.hackathon.skillsift.entity.JobEntity;
import org.hackathon.skillsift.entity.ResumeEntity;
import org.hackathon.skillsift.repository.JobRepository;
import org.hackathon.skillsift.repository.ResumeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.mock.web.MockMultipartFile;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

@Service
public class SkillSiftService {

    @Autowired
    JobRepository jobRepository;

    @Autowired
    ResumeRepository resumeRepository;

    List<Integer> jobIds = Arrays.asList(134, 345, 569);

    public void service() {
        JobEntity jobEntity1 = new JobEntity();
        jobEntity1.setJobId(134);
        jobEntity1.setJobDescription("Java Developers Reqd");

        JobEntity jobEntity2 = new JobEntity();
        jobEntity2.setJobId(345);
        jobEntity2.setJobDescription("Python Developers Reqd");

        JobEntity jobEntity3 = new JobEntity();
        jobEntity3.setJobId(569);
        jobEntity3.setJobDescription("C++ Developers Reqd");

        jobRepository.save(jobEntity1);
        jobRepository.save(jobEntity2);
        jobRepository.save(jobEntity3);

    }

    public void resumeService() throws IOException {
        String folderPath = "src/main/resources/resume";
        File folder = new File(folderPath);

        if (folder.exists() && folder.isDirectory()) {
            File[] files = folder.listFiles();

            if (files != null) {
                // Iterate through the array and print the names of the files
                for (File file : files) {
                    Random random = new Random();
                    int randomInt = random.nextInt(3);
                    Integer jobId = jobIds.get(randomInt);
                    saveResume(folderPath + "/" + file.getName(), file.getName(), jobRepository.findById(jobId).get());

                }
            } else {
                System.out.println("The folder is empty.");
            }
        } else {
            System.out.println("Invalid folder path or folder does not exist.");
        }
    }

    public void saveResume(String pathToFile, String fileName, JobEntity jobEntity) throws IOException {
        MultipartFile multipartFile1 = new MockMultipartFile(fileName, new FileInputStream(pathToFile));

        ResumeEntity resume = new ResumeEntity();
        resume.setFileName(multipartFile1.getOriginalFilename());
        resume.setFileData(multipartFile1.getBytes());
        resume.setJob(jobEntity);

        resumeRepository.save(resume);
    }
}
