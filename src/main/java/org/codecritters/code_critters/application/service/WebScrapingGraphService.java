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

@Service
public class WebScrapingGraphService {

    // Add your DeepL API key here
    private static final String DEEPL_API_KEY = "44c6646f-25ac-4abc-ab50-58b28d62aaef:fx";

    // Method to scrape content from the W3Schools Graphs page and return HTML content as a String
    public String scrapeW3SchoolsGraphPage() throws IOException {
        String url = "https://www.w3schools.com/dsa/dsa_theory_graphs.php";

        try {
            // Fetch the document from the URL
            Document doc = Jsoup.connect(url).get();

            // Select the main content area
            Element content = doc.selectFirst("div.w3-col.l10.m12");

            if (content != null) {
                // Process images: convert to absolute URLs and set size
                for (Element img : content.select("img")) {
                    img.attr("src", img.absUrl("src"));
                    img.attr("width", "200");
                }

                // Remove unwanted elements
                content.select("svg, .-user-session-btns, .user-progress-star, " +
                        ".user-progress-point, .w3-right.w3-btn, .w3-left.w3-btn, " +
                        "a[title=Previous], a[title=Next], #user-profile-bottom-wrapper, " +
                        "#menu, #w3-exerciseform, a").remove();

                // Remove specific text
                removeText(content, "DSA Exercises");
                removeText(content, "Next Chapter");

                // Return the cleaned HTML content as a String
                return content.html();
            } else {
                return "Content not available.";
            }

        } catch (IOException e) {
            throw new IOException("Error fetching content: " + e.getMessage(), e);
        }
    }

    // Utility method to remove specific text from the content
    private void removeText(Element content, String text) {
        content.getElementsContainingOwnText(text).remove();
    }

    // Method to translate HTML content using DeepL API
    public String translateHtmlContent(String htmlContent, String targetLang) throws IOException {
        System.out.println("Translating content to language: " + targetLang);

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
                System.out.println("DeepL API response status: " + statusCode);

                if (statusCode == 200) {
                    // Parse the response JSON to extract the translated text
                    ObjectMapper objectMapper = new ObjectMapper();
                    JsonNode jsonNode = objectMapper.readTree(response.getEntity().getContent());
                    String translatedText = jsonNode.get("translations").get(0).get("text").asText();
                    System.out.println("Translation successful.");
                    return translatedText;
                } else {
                    // Log the error response
                    String errorResponse = EntityUtils.toString(response.getEntity(), StandardCharsets.UTF_8);
                    System.err.println("Failed to translate text. HTTP error code: " + statusCode + ". Response: " + errorResponse);
                    throw new IOException("Failed to translate text. HTTP error code: " + statusCode);
                }
            }
        } catch (Exception e) {
            System.err.println("Exception during translation: " + e.getMessage());
            throw new IOException("Exception during translation: " + e.getMessage(), e);
        }
    }

    // Method to get the content in the specified language
    public String getContentInLanguage(String lang) throws IOException {
        String htmlContent = scrapeW3SchoolsGraphPage();
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

    // Method to scrape and generate the PDF in the specified language
    public byte[] scrapeAndGeneratePdf(String lang) throws IOException {
        String htmlContent = getContentInLanguage(lang);

        // Wrap content into an HTML format
        String html = "<html><head><title>Graph Theory</title></head><body>" + htmlContent + "</body></html>";

        // Convert HTML content to PDF and return as byte array
        return convertHtmlToPdf(html);
    }

    // Method to convert HTML content to a PDF and return the PDF as a byte array
    private byte[] convertHtmlToPdf(String htmlContent) throws IOException {
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            // Setup converter properties
            ConverterProperties converterProperties = new ConverterProperties();

            // Convert HTML to PDF and write to byte array output stream
            HtmlConverter.convertToPdf(htmlContent, baos, converterProperties);

            // Return the PDF as a byte array
            return baos.toByteArray();
        }
    }
}