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
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Logger;

@Service
public class WebScrapingHashService {

    private final static Logger LOGGER = Logger.getLogger(WebScrapingHashService.class.getName());

    // Add your DeepL API key here
    private static final String DEEPL_API_KEY = "44c6646f-25ac-4abc-ab50-58b28d62aaef:fx";

    // Method to scrape content from the W3Schools Hash Tables page
    public String scrapeW3SchoolsHashPage() throws IOException {
        String url = "https://www.w3schools.com/dsa/dsa_theory_hashtables.php";

        try {
            // Fetch the document from the URL
            LOGGER.info("Fetching content from URL: " + url);
            Document doc = Jsoup.connect(url).get();

            // Select the main content area
            Element content = doc.selectFirst("div.w3-col.l10.m12");

            if (content != null) {
                // Process images: convert to absolute URLs and set size
                for (Element img : content.select("img")) {
                    img.attr("src", img.absUrl("src"));
                    img.attr("width", "200");
                }

                // Remove unwanted elements, including hrefs and specific elements
                content.select("svg, .-user-session-btns, .user-progress-star, " +
                               ".user-progress-point, .w3-right.w3-btn, .w3-left.w3-btn, " +
                               "a[title=Previous], a[title=Next], #user-profile-bottom-wrapper, " +
                               "#w3-exerciseform, .w3-btn.w3-margin-bottom, #vueApp2, #vueApp, " +
                               "a[href='dsa_theory_linkedlists_memory.php'], a").remove();

                // Remove specific text
                removeText(content, "Where to Start?");
                removeText(content, "In this tutorial, you will first learn about a data structure with matching algorithms, before moving on to the next data structure.");
                removeText(content, "Further into the tutorial the concepts become more complex, and it is therefore a good idea to learn DSA by doing the tutorial step-by-step from the start.");
                removeText(content, "And as mentioned on the previous page, you should be comfortable in at least one of the most common programming languages, like for example ");
                removeText(content, "href=\"/js/default.asp\"");
                removeText(content, "href=\"/c/default.asp\"");
                removeText(content, "On the next page we will look at two different algorithms that prints out the first 100 Fibonacci numbers using only primitive data structures (two integer variables). One algorithm uses a loop, and one algorithm uses something called recursion.");
                removeText(content, "Click the 'Next' button to continue.");
                removeText(content, "DSA Exercises");

                // Return the cleaned content
                return content.html();
            } else {
                LOGGER.severe("Content not available.");
                return "Content not available.";
            }

        } catch (IOException e) {
            LOGGER.severe("Error fetching content: " + e.getMessage());
            throw new IOException("Error fetching content: " + e.getMessage(), e);
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
        String htmlContent = scrapeW3SchoolsHashPage();
        if (htmlContent.equals("Content not available.")) {
            throw new IOException("Content not available.");
        }

        // Check if the language is supported
        List<String> supportedLanguages = Arrays.asList("EN", "DE", "AR");
        if (!supportedLanguages.contains(lang.toUpperCase())) {
            throw new IOException("Language not supported: " + lang);
        }

        if (!lang.equalsIgnoreCase("EN")) {
            // Translate content to the specified language
            htmlContent = translateHtmlContent(htmlContent, lang.toUpperCase());
        }

        return htmlContent;
    }

    // Method to convert HTML to PDF
    public byte[] convertHtmlToPdf(String htmlContent) throws IOException {
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            ConverterProperties converterProperties = new ConverterProperties();
            HtmlConverter.convertToPdf(htmlContent, baos, converterProperties);
            return baos.toByteArray();
        }
    }

    // Method to scrape and generate the PDF in the specified language
    public byte[] scrapeAndGeneratePdf(String lang) throws IOException {
        String htmlContent = getContentInLanguage(lang);

        // Wrap content into an HTML format
        String html = "<html><head><title>Hash Tables</title></head><body>" + htmlContent + "</body></html>";

        // Convert HTML content to PDF and return as byte array
        return convertHtmlToPdf(html);
    }

    // Utility method to remove specific text from the content
    private void removeText(Element content, String text) {
        content.getElementsContainingOwnText(text).remove();
    }
}
