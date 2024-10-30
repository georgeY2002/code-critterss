package org.codecritters.code_critters.web.controller;

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
import org.codecritters.code_critters.application.service.WebScrappingQuiz1Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

/**
 * REST Controller for handling quiz-related requests.
 */
@RestController
@RequestMapping("/api/quiz")
public class WebScrappingQuiz1Controller {

    private final WebScrappingQuiz1Service quizService;
    private final ObjectMapper objectMapper;
    private final Logger LOGGER = Logger.getLogger(WebScrappingQuiz1Controller.class.getName());

    @Autowired
    public WebScrappingQuiz1Controller(WebScrappingQuiz1Service quizService, ObjectMapper objectMapper) {
        this.quizService = quizService;
        this.objectMapper = objectMapper;
    }

    /**
     * Endpoint to retrieve quiz questions in JSON format.
     *
     * @param lang Optional query parameter to specify the language (e.g., "EN", "DE", "AR").
     *             Defaults to "EN" if not provided.
     * @return ResponseEntity containing the quiz data in JSON or an error message.
     */
    @GetMapping
    public ResponseEntity<?> getQuizJson(@RequestParam(value = "lang", defaultValue = "EN") String lang) {
        try {
            LOGGER.info("Received request to fetch quiz in language: " + lang);
            String content = quizService.getContentInLanguage(lang);

            // If language is English, return the JSON directly
            if (lang.equalsIgnoreCase("EN")) {
                JsonNode jsonNode = objectMapper.readTree(content);
                return ResponseEntity.ok(jsonNode);
            } else {
                // For other languages, assuming the service returns translated JSON as a string
                // Parse it back to JsonNode
                JsonNode translatedJson = objectMapper.readTree(content);
                return ResponseEntity.ok(translatedJson);
            }
        } catch (IOException e) {
            LOGGER.severe("Error fetching quiz: " + e.getMessage());
            return ResponseEntity.status(500).body("Internal Server Error: " + e.getMessage());
        }
    }

    /**
     * Endpoint to retrieve quiz questions as an HTML page.
     *
     * @param lang Optional query parameter to specify the language (e.g., "EN", "DE", "AR").
     *             Defaults to "EN" if not provided.
     * @return ResponseEntity containing the HTML content or an error message.
     */
    @GetMapping("/html")
    public ResponseEntity<?> getQuizHtml(@RequestParam(value = "lang", defaultValue = "EN") String lang) {
        try {
            LOGGER.info("Received request to fetch quiz HTML in language: " + lang);
            String jsonContent = quizService.scrapeW3SchoolsQuizPage();

            // Parse the JSON content to a list of JsonNodes
            JsonNode jsonNode = objectMapper.readTree(jsonContent);
            List<JsonNode> questions = new ArrayList<>();
            if (jsonNode.isArray()) {
                for (JsonNode node : jsonNode) {
                    questions.add(node);
                }
            }

            // Generate HTML from questions
            String htmlContent = quizService.generateHtmlFromQuestions(questions);

            // Translate HTML if needed
            if (!lang.equalsIgnoreCase("EN")) {
                htmlContent = quizService.translateHtmlContent(htmlContent, lang.toUpperCase());
            }

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.TEXT_HTML);
            return ResponseEntity.ok().headers(headers).body(htmlContent);
        } catch (IOException e) {
            LOGGER.severe("Error fetching quiz HTML: " + e.getMessage());
            return ResponseEntity.status(500).body("Internal Server Error: " + e.getMessage());
        }
    }

    /**
     * Endpoint to retrieve a specific part of the quiz questions.
     * Useful if the quiz is split into multiple sections.
     *
     * @param part      The part number to retrieve.
     * @param splitSizes The sizes defining how the quiz is split.
     * @param lang      Optional query parameter to specify the language.
     * @return ResponseEntity containing the specified part of the quiz or an error message.
     */
    @GetMapping("/part/{partNumber}")
    public ResponseEntity<?> getQuizPart(
            @PathVariable("partNumber") int partNumber,
            @RequestParam(value = "splitSizes") int[] splitSizes,
            @RequestParam(value = "lang", defaultValue = "EN") String lang) {
        try {
            LOGGER.info("Received request to fetch part " + partNumber + " of the quiz in language: " + lang);
            String jsonContent = quizService.scrapeW3SchoolsQuizPage();

            // Parse the JSON content to a list of JsonNodes
            JsonNode jsonNode = objectMapper.readTree(jsonContent);
            List<JsonNode> allQuestions = new ArrayList<>();
            if (jsonNode.isArray()) {
                for (JsonNode node : jsonNode) {
                    allQuestions.add(node);
                }
            }

            // Split questions based on splitSizes
            List<List<JsonNode>> parts = quizService.splitQuestions(allQuestions, splitSizes);

            if (partNumber < 1 || partNumber > parts.size()) {
                return ResponseEntity.status(400).body("Invalid part number. Must be between 1 and " + parts.size());
            }

            List<JsonNode> selectedPart = parts.get(partNumber - 1);
            String partJson = objectMapper.writeValueAsString(selectedPart);

            // Translate if needed
            if (!lang.equalsIgnoreCase("EN")) {
                partJson = quizService.translateHtmlContent(partJson, lang.toUpperCase());
                // Assuming the translated content is still JSON formatted
                JsonNode translatedJson = objectMapper.readTree(partJson);
                return ResponseEntity.ok(translatedJson);
            }

            JsonNode responseJson = objectMapper.readTree(partJson);
            return ResponseEntity.ok(responseJson);
        } catch (IOException e) {
            LOGGER.severe("Error fetching quiz part: " + e.getMessage());
            return ResponseEntity.status(500).body("Internal Server Error: " + e.getMessage());
        }
    }

    /**
     * Global exception handler for handling unexpected exceptions.
     *
     * @param ex The exception that was thrown.
     * @return ResponseEntity containing the error message.
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<?> handleException(Exception ex) {
        LOGGER.severe("Unhandled exception: " + ex.getMessage());
        return ResponseEntity.status(500).body("Internal Server Error: " + ex.getMessage());
    }
}
