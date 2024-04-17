package org.hackathon.skillsift.service;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.apache.http.client.methods.HttpDelete;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.hackathon.skillsift.entity.ResumeEntity;
import org.hackathon.skillsift.repository.JobRepository;
import org.hackathon.skillsift.repository.ResumeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.stereotype.Service;

import org.apache.http.HttpEntity;
import org.apache.http.HttpHeaders;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class ChatGptService {
    
    @Autowired
    JobRepository jobRepository;

    @Autowired
    ResumeRepository resumeRepository;

    List<Integer> jobIds = Arrays.asList(134, 345, 569);

    public void service() throws IOException {
        String folderPath = "src/main/resources/resume";
        File folder = new File(folderPath);

        if (folder.exists() && folder.isDirectory()) {
            File[] files = folder.listFiles();

            if (files != null) {
                // Iterate through the array and print the names of the files
                for (File file : files) {
                    readFile(folderPath + "/" + file.getName(), file.getName());

                }
            } else {
                System.out.println("The folder is empty.");
            }
        } else {
            System.out.println("Invalid folder path or folder does not exist.");
        }

    }

    public void readFile(String pathToFile, String fileName) throws IOException {
        String resumeText = pdfToString(pathToFile);
        if (resumeText == null) {
            System.out.println("Error extracting text from PDF.");
            return;
        }

       String finalTest = convertToSingleLine(resumeText);

        ResumeEntity resumeEntity = new ResumeEntity();

        Random random = new Random();
        int randomInt = random.nextInt(3);
        Integer jobId = jobIds.get(randomInt);

        MultipartFile multipartFile1 = new MockMultipartFile(fileName, new FileInputStream(pathToFile));
        
        resumeEntity.setFileName(fileName);
        resumeEntity.setFileData(multipartFile1.getBytes());
        
        resumeEntity.setJob(jobRepository.findById(jobId).get());

        String techRequest = "{\n" +
                "  \"prompt\": \"" + finalTest + "Hello! I'm analyzing a resume and need to identify the programming languages mentioned. Please provide a list of programming languages mentioned in the resume, separated by commas" + "\",\n" +
                "  \"max_tokens\": 100\n" + // Adjust max tokens as needed
                "}";

        String tech = sendRequest(techRequest);
        System.out.println(tech);
        resumeEntity.setTechnologies(tech);

        String collegeRequest = "{\n" +
                "  \"prompt\": \"" + finalTest + "Extract the college name from the resume text. The college name is typically associated with higher education institutions and degrees, such as universities, colleges, or institutes. Exclude any references to secondary education or schools." + "\",\n" +
                "  \"max_tokens\": 50\n" +
                "}";

//        The name of the college of the person for highest or latest level of is:

        String college = sendRequest(collegeRequest);
        String collegeStringPattern = "[^a-zA-Z\\s]";
        Pattern p1 = Pattern.compile(collegeStringPattern);
        Matcher collegeName = p1.matcher(college);
        String finalCollegeName =  collegeName.replaceAll("");
        System.out.println(finalCollegeName);
        resumeEntity.setCollege(finalCollegeName);

        String yoeRequest = "{\n" +
                "  \"prompt\": \"" + finalTest + " Just answer in integer. Do not add academic experience. The professional work experience of the person in years is: " + "\",\n" +
                "  \"max_tokens\": 3\n" + // Adjust max tokens as needed
                "}";

        String yoe = sendRequest(yoeRequest);
        if (yoe == null) {
            yoe = "0";
        }

        Pattern pattern = Pattern.compile("\\d+");
        Matcher matcher = pattern.matcher(yoe);

        // StringBuilder to store extracted digits
        StringBuilder extractedDigits = new StringBuilder();

        // Loop through matches and append to StringBuilder
        while (matcher.find()) {
            extractedDigits.append(matcher.group());
        }

        // Convert StringBuilder to String
        String yoeResult = extractedDigits.toString();
        System.out.println(yoeResult);
        resumeEntity.setYearsOfExperience(Integer.valueOf(yoeResult));

        String nameRequest = "{\n" +
                "  \"prompt\": \"" + finalTest + " ---The name of the person is:" + "\",\n" +
                "  \"max_tokens\": 7\n" +
                "}";

        String name = sendRequest(nameRequest);
        if (name == null) {
            resumeEntity.setName(name);
        } else {
            String stringPattern = "[^a-zA-Z0-9\\s]";
            Pattern p = Pattern.compile(stringPattern);
            Matcher m = p.matcher(name);
            String finalName =  m.replaceAll("");

            System.out.println(finalName);
            resumeEntity.setName(finalName.toUpperCase());
        }

        resumeRepository.save(resumeEntity);
    }

    public String sendRequest(String request) throws IOException {

        HttpClient httpClient = HttpClients.createDefault();
        String apiKey = "";
        String endpoint = "";

        HttpPost httpRequest = new HttpPost(endpoint);
        httpRequest.addHeader(HttpHeaders.AUTHORIZATION, "Bearer " + apiKey);
        httpRequest.addHeader(HttpHeaders.CONTENT_TYPE, "application/json");
        httpRequest.setEntity(new StringEntity(request));

        HttpResponse response = httpClient.execute(httpRequest);
        HttpEntity entity = response.getEntity();
        if (entity != null) {
            String result = EntityUtils.toString(entity);
            return extractTextFromJson(result);
        }

        return null;
    }

    public String pdfToString(String pdfFilePath) {
        File file = new File(pdfFilePath);
        try (PDDocument document = PDDocument.load(new FileInputStream(file))) {
            PDFTextStripper pdfStripper = new PDFTextStripper();
            String text = pdfStripper.getText(document);
            return text;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    private String convertToSingleLine(String text) {
        // Remove newlines
        String singleLineString = text.replaceAll("\\r?\\n", " ");
        String finalString = singleLineString.replaceAll("\\\\n|\\\\", " ");

        return finalString;
    }

    private String extractTextFromJson(String jsonString) {
        JsonParser jsonParser = new JsonParser();
        JsonObject jsonObject = jsonParser.parse(jsonString).getAsJsonObject();

        if (jsonObject.has("choices") && jsonObject.get("choices").isJsonArray()) {
            // Get the choices array
            var choicesArray = jsonObject.getAsJsonArray("choices");

            if (choicesArray.size() > 0) {
                // Get the first element from choices array
                var firstChoice = choicesArray.get(0).getAsJsonObject();

                if (firstChoice.has("text") && firstChoice.get("text").isJsonPrimitive()) {
                    // Get the text value
                    return firstChoice.get("text").getAsString();
                }
            }
        }

        return null;
    }
}

