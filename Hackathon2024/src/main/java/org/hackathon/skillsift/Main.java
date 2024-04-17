package org.hackathon.skillsift;

import org.hackathon.skillsift.service.ChatGptService;
import org.hackathon.skillsift.service.SkillSiftService;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;

import java.io.IOException;

@SpringBootApplication
public class Main {

    public static void main(String[] args) throws IOException {
        ApplicationContext context = SpringApplication.run(Main.class, args);
        SkillSiftService service = context.getBean(SkillSiftService.class);
        ChatGptService chatGptService = context.getBean(ChatGptService.class);
        service.service();
//        service.resumeService();
//        chatGptService.clearCache();
        chatGptService.service();



    }
}