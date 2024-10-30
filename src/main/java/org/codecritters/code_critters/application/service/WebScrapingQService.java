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
import com.itextpdf.html2pdf.ConverterProperties;
import com.itextpdf.html2pdf.HtmlConverter;
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

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Logger;

@Service
public class WebScrapingQService {

    private final static Logger LOGGER = Logger.getLogger(WebScrapingQService.class.getName());

    // Add your DeepL API key here
    private static final String DEEPL_API_KEY = "44c6646f-25ac-4abc-ab50-58b28d62aaef:fx"; // Replace with your actual DeepL API key

    // Method to scrape content from the W3Schools Queues page
    public String scrapeDataQueuesPage() throws IOException {
        String url = "https://www.w3schools.com/dsa/dsa_data_queues.php"; // Correct URL for queues

        try {
            // Fetch the document from the URL
            LOGGER.info("Fetching content from URL: " + url);
            Document doc = Jsoup.connect(url)
                                .userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/58.0.3029.110 Safari/537.36")
                                .timeout(10000) // 10-second timeout
                                .get();

            // Select the main content area
            Element content = doc.selectFirst("div.w3-col.l10.m12");

            if (content != null) {
                // Process images: convert to absolute URLs and set size
                for (Element img : content.select("img")) {
                    img.attr("src", img.absUrl("src"));
                    img.attr("width", "200"); // Resize images
                }

                // Remove unwanted elements
                removeElementsByClass(content, "user-authenticated w3-hide");
                removeElementsByClass(content, "-title");
                removeElementsByClass(content, "-headline");
                removeElementsByClass(content, "-right-side-section");
                removeElementsByClass(content, "-user-session-btns");
                removeElementsByClass(content, "w3-btn w3-margin-bottom");
                removeElementsByClass(content, "w3-clear nextprev");
                removeElementsById(content, "vueApp");
                removeElementsById(content, "vueApp3");
                removeElementsById(content, "vueApp4");
                removeElementsById(content, "vueApp2");

                // Remove the anchor elements
                removeElementsByTag(content, "a");

                // Remove elements with specific class and id
                removeElementsByClass(content, "exercisewindow");
                removeElementsById(content, "w3-exerciseform");

                // Remove the text "DSA Exercises"
                removeTextContaining(content, "DSA Exercises");

                // Return the cleaned content as HTML string
                return content.html();
            } else {
                LOGGER.severe("Content not available.");
                return "Content not available.";
            }

        } catch (IOException e) {
            LOGGER.severe("Error fetching content: " + e.getMessage());
            throw new IOException("Error fetching content from the URL: " + url + ". Error: " + e.getMessage(), e);
        }
    }

    // Method to translate HTML content using DeepL API
    public String translateHtmlContent(String htmlContent, String targetLang) throws IOException {
        LOGGER.info("Translating content to language: " + targetLang);

        String url = "https://api-free.deepl.com/v2/translate";

        // Prepare the parameters
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
                // Check for successful response
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

    // Method to get the content in the specified language
    public String getContentInLanguage(String lang) throws IOException {
        String htmlContent = scrapeDataQueuesPage();
        if (htmlContent.equals("Content not available.")) {
            throw new IOException("Content not available.");
        }

        // Check if the language is supported
        List<String> supportedLanguages = Arrays.asList("EN", "DE", "AR"); // Add more as needed
        if (!supportedLanguages.contains(lang.toUpperCase())) {
            throw new IOException("Language not supported: " + lang);
        }

        if (!lang.equalsIgnoreCase("EN")) {
            // Translate content to the specified language
            htmlContent = translateHtmlContent(htmlContent, lang.toUpperCase());
        }

        return htmlContent;
    }

    // Method to get the scraped content as PDF in the specified language
    public byte[] getScrapedContentAsPdf(String lang) throws IOException {
        String htmlContent = getContentInLanguage(lang);

        // Wrap content into an HTML format
        String html = "<html><head><title>Data Queues</title></head><body>" + htmlContent + "</body></html>";

        // Convert HTML content to PDF and return as byte array
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            ConverterProperties converterProperties = new ConverterProperties();
            HtmlConverter.convertToPdf(html, baos, converterProperties);
            return baos.toByteArray();
        }
    }

    // Utility methods to remove unwanted elements

    // Helper method to remove elements by class
    private void removeElementsByClass(Element content, String className) {
        Elements elements = content.getElementsByClass(className);
        if (elements != null) {
            elements.remove();
        }
    }

    // Helper method to remove elements by ID
    private void removeElementsById(Element content, String id) {
        Element element = content.getElementById(id);
        if (element != null) {
            element.remove();
        }
    }

    // Helper method to remove elements by tag (e.g., 'a' for anchor tags)
    private void removeElementsByTag(Element content, String tagName) {
        Elements elements = content.getElementsByTag(tagName);
        if (elements != null) {
            elements.remove();
        }
    }

    // Helper method to remove any element that contains specific text
    private void removeTextContaining(Element content, String text) {
        Elements elements = content.getElementsContainingOwnText(text);
        if (elements != null) {
            elements.remove();
        }
    }
}
