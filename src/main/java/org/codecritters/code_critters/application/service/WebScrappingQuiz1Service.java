package org.codecritters.code_critters.application.service;

/*-
 * #%L
 * Code Critters
 * %%
 * Copyright (C) 2019 - 2024 Michael Gruber
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/gpl-3.0.html>.
 * #L%
 */

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Logger;

@Service
public class WebScrappingQuiz1Service {

    private final static Logger LOGGER = Logger.getLogger(WebScrappingQuiz1Service.class.getName());

    // DeepL API key for translation
    private static final String DEEPL_API_KEY = "44c6646f-25ac-4abc-ab50-58b28d62aaef:fx"; // Replace with your actual DeepL API key

    // Supported languages for translation
    private static final List<String> SUPPORTED_LANGUAGES = Arrays.asList("EN", "DE", "AR"); // Add more if needed

    /**
     * Method to scrape quiz questions from the W3Schools Quiz Page.
     *
     * @return JSON-formatted quiz questions
     * @throws IOException If an error occurs during scraping
     */
    public String scrapeW3SchoolsQuizPage() throws IOException {
        String url = "https://www.w3schools.com/quiztest/quiztest.php?qtest=DSA";

        try {
            LOGGER.info("Fetching content from URL: " + url);
            Document doc = Jsoup.connect(url)
                    .userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/58.0.3029.110 Safari/537.36")
                    .timeout(10000)
                    .get();

            Elements questions = doc.select("div.question");
            List<JsonNode> quizData = new ArrayList<>();
            ObjectMapper objectMapper = new ObjectMapper();  // For converting to JSON

            for (Element question : questions) {
                ObjectNode questionNode = objectMapper.createObjectNode(); // Create a JSON node for each question
                questionNode.put("question", question.text()); // Add question text

                // Create an ArrayNode for the options
                ArrayNode optionsArray = objectMapper.createArrayNode();

                // Get related options (adjust the logic according to the actual structure)
                Elements optionElements = question.siblingElements();
                for (Element option : optionElements) {
                    optionsArray.add(option.text()); // Add each option to the ArrayNode
                }

                // Attach the options array to the question node
                questionNode.set("options", optionsArray);

                // Add the question node to the quizData list
                quizData.add(questionNode);
            }

            // Return the JSON string representation of the quizData list
            return objectMapper.writeValueAsString(quizData);
        } catch (IOException e) {
            LOGGER.severe("Error fetching content: " + e.getMessage());
            throw new IOException("Error fetching content from the URL: " + url + ". Error: " + e.getMessage(), e);
        }
    }

    /**
     * Method to translate HTML content using the DeepL API.
     *
     * @param htmlContent The original HTML content
     * @param targetLang  The target language code (e.g., "DE", "AR")
     * @return Translated HTML content as a String
     * @throws IOException If an error occurs during translation
     */
    public String translateHtmlContent(String htmlContent, String targetLang) throws IOException {
        LOGGER.info("Translating content to language: " + targetLang);

        String url = "https://api-free.deepl.com/v2/translate";

        // Prepare the parameters for the DeepL API
        String encodedHtmlContent = URLEncoder.encode(htmlContent, StandardCharsets.UTF_8.name());
        String params = "auth_key=" + DEEPL_API_KEY
                + "&text=" + encodedHtmlContent
                + "&target_lang=" + targetLang
                + "&tag_handling=xml"
                + "&ignore_tags=img";

        // Create the HTTP client
        try (CloseableHttpClient client = HttpClients.createDefault()) {
            // Create the HTTP POST request
            HttpPost httpPost = new HttpPost(url);
            httpPost.setHeader("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8");
            httpPost.setEntity(new StringEntity(params, StandardCharsets.UTF_8));

            // Execute the request
            try (CloseableHttpResponse response = client.execute(httpPost)) {
                int statusCode = response.getStatusLine().getStatusCode();
                LOGGER.info("DeepL API response status: " + statusCode);

                if (statusCode == 200) {
                    // Parse the response JSON to extract the translated text
                    ObjectMapper objectMapper = new ObjectMapper();
                    JsonNode jsonNode = objectMapper.readTree(response.getEntity().getContent());
                    String translatedText = jsonNode.get("translations").get(0).get("text").asText();
                    LOGGER.info("Translation successful.");
                    return translatedText;
                } else {
                    // Log the error response
                    String errorResponse = EntityUtils.toString(response.getEntity(), StandardCharsets.UTF_8);
                    LOGGER.severe("Failed to translate text. HTTP error code: " + statusCode + ". Response: " + errorResponse);
                    throw new IOException("Failed to translate text. HTTP error code: " + statusCode);
                }
            }
        } catch (Exception e) {
            LOGGER.severe("Exception during translation: " + e.getMessage());
            throw new IOException("Exception during translation: " + e.getMessage(), e);
        }
    }

    /**
     * Method to get quiz content in the specified language.
     *
     * @param lang The language code (e.g., "EN", "DE", "AR")
     * @return The quiz content in the specified language
     * @throws IOException If an error occurs during scraping or translation
     */
    public String getContentInLanguage(String lang) throws IOException {
        String htmlContent = scrapeW3SchoolsQuizPage();
        if (htmlContent.equals("Content not available.")) {
            throw new IOException("Content not available.");
        }

        // Check if the language is supported
        if (!SUPPORTED_LANGUAGES.contains(lang.toUpperCase())) {
            throw new IOException("Language not supported: " + lang);
        }

        if (!lang.equalsIgnoreCase("EN")) {
            // Translate content to the specified language
            htmlContent = translateHtmlContent(htmlContent, lang.toUpperCase());
        }

        return htmlContent;
    }

    /**
     * Utility method to split the list of questions into parts (for different HTML files).
     *
     * @param allQuestions List of all questions as JSON nodes
     * @param splitSizes   Array of split sizes (e.g., {5, 10, 10})
     * @return List of lists, each containing a part of the questions
     */
    public List<List<JsonNode>> splitQuestions(List<JsonNode> allQuestions, int[] splitSizes) {
        int start = 0;
        List<List<JsonNode>> parts = new ArrayList<>();

        for (int size : splitSizes) {
            List<JsonNode> subList = allQuestions.subList(start, Math.min(start + size, allQuestions.size()));
            parts.add(subList);
            start += size;
        }

        return parts;
    }

    /**
     * Utility method to generate HTML from a list of quiz questions.
     *
     * @param questions The list of quiz questions as JSON nodes
     * @return HTML string representing the questions
     */
    public String generateHtmlFromQuestions(List<JsonNode> questions) {
        StringBuilder htmlBuilder = new StringBuilder();
        htmlBuilder.append("<html><head><title>Quiz</title></head><body>");
        htmlBuilder.append("<div class='quiz'>");

        for (JsonNode question : questions) {
            htmlBuilder.append("<div class='question'>");
            htmlBuilder.append("<h3>").append(question.get("question").asText()).append("</h3>");
            htmlBuilder.append("<ul>");
            ArrayNode options = (ArrayNode) question.get("options");
            for (JsonNode option : options) {
                htmlBuilder.append("<li>").append(option.asText()).append("</li>");
            }
            htmlBuilder.append("</ul>");
            htmlBuilder.append("</div>");
        }

        htmlBuilder.append("</div></body></html>");
        return htmlBuilder.toString();
    }
}
