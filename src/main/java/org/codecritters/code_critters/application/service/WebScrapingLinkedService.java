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
import com.itextpdf.html2pdf.resolver.font.DefaultFontProvider;
import com.itextpdf.html2pdf.HtmlConverter;
import com.itextpdf.io.font.FontProgram;
import com.itextpdf.io.font.FontProgramFactory;
import com.itextpdf.layout.font.FontProvider;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Logger;

@Service
public class WebScrapingLinkedService {

    private final static Logger LOGGER = Logger.getLogger(WebScrapingLinkedService.class.getName());

    // Add your DeepL API key here
    private static final String DEEPL_API_KEY = "44c6646f-25ac-4abc-ab50-58b28d62aaef:fx";

    // Method to scrape content from the Linked List page
    public String scrapeLinkedListPage() throws IOException {
        String url = "https://www.w3schools.com/dsa/dsa_theory_linkedlists.php"; // Correct URL for linked lists

        try {
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

                // Remove unwanted elements
                content.select("svg, .-user-session-btns, .user-progress-star, " +
                        ".user-progress-point, .w3-right.w3-btn, .w3-left.w3-btn, " +
                        "a[title=Previous], a[title=Next], #user-profile-bottom-wrapper, .exercisewindow").remove();

                // Remove all anchor (a) tags
                content.select("a").remove();

                // Remove specific text
                removeText(content, "Where to Start?");
                removeText(content, "In this tutorial, you will first learn about a data structure with matching algorithms, before moving on to the next data structure.");
                removeText(content, "Test Yourself With Exercises");
                removeText(content, "DSA Exercises");

                LOGGER.info("Content cleaned and returned.");
                return content.html();
            } else {
                LOGGER.warning("Content not available.");
                return "Content not available.";
            }
        } catch (IOException e) {
            LOGGER.severe("Error fetching content: " + e.getMessage());
            throw new IOException("Error fetching content: " + e.getMessage(), e);
        }
    }

    // Utility method to remove specific text from the content
    private void removeText(Element content, String text) {
        content.getElementsContainingOwnText(text).remove();
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
        String htmlContent = scrapeLinkedListPage();
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

        // Wrap content into an HTML format with proper direction and font
        String html = generateHtmlContent(htmlContent, lang);

        // Convert HTML content to PDF and return as byte array
        return convertHtmlToPdf(html, lang);
    }

    // Method to generate HTML with proper direction and font settings
    private String generateHtmlContent(String content, String lang) {
        String direction = "ltr";
        String fontFamily = "Arial, sans-serif"; // Default font

        if (lang.equalsIgnoreCase("AR")) {
            direction = "rtl";
            fontFamily = "'Noto Naskh Arabic', Arial, sans-serif";
        }

        return "<html><head><meta charset=\"UTF-8\">" +
                "<style>" +
                "body { direction: " + direction + "; font-family: " + fontFamily + "; }" +
                "</style>" +
                "</head><body>" + content + "</body></html>";
    }

    // Method to convert HTML content to a PDF and return the PDF as a byte array
    private byte[] convertHtmlToPdf(String htmlContent, String lang) throws IOException {
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            // Setup converter properties
            ConverterProperties converterProperties = new ConverterProperties();

            // Create a FontProvider
            DefaultFontProvider fontProvider = new DefaultFontProvider(false, false, false);

            // Add standard fonts and system fonts
            fontProvider.addStandardPdfFonts();
            fontProvider.addSystemFonts();

            // Load Noto Naskh Arabic font for Arabic text
            if (lang.equalsIgnoreCase("AR")) {
                // Load the font from resources
                try (InputStream fontStream = new ClassPathResource("fonts/NotoNaskhArabic-Regular.ttf").getInputStream()) {
                    // Read all bytes from the InputStream
                    byte[] fontBytes = toByteArray(fontStream);

                    // Create a FontProgram from the font bytes
                    FontProgram fontProgram = FontProgramFactory.createFont(fontBytes);

                    // Add the font to the FontProvider
                    fontProvider.addFont(fontProgram);

                    LOGGER.info("Arabic font loaded successfully.");
                } catch (Exception e) {
                    LOGGER.severe("Failed to load Arabic font: " + e.getMessage());
                    throw new IOException("Failed to load Arabic font", e);
                }
            }

            // Set the FontProvider in the converter properties
            converterProperties.setFontProvider(fontProvider);

            // Set base URI to handle relative paths in HTML (e.g., for images)
            converterProperties.setBaseUri("https://www.w3schools.com/dsa/");

            // Convert HTML to PDF and write to byte array output stream
            HtmlConverter.convertToPdf(htmlContent, baos, converterProperties);

            // Return the PDF as a byte array
            return baos.toByteArray();
        }
    }

    // Helper method to read all bytes from an InputStream (compatible with Java 8)
    private byte[] toByteArray(InputStream input) throws IOException {
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        int nRead;
        byte[] data = new byte[16384]; // 16KB buffer

        while ((nRead = input.read(data, 0, data.length)) != -1) {
            buffer.write(data, 0, nRead);
        }

        buffer.flush();
        return buffer.toByteArray();
    }
    // Method to generate a simple Arabic PDF for testing
public byte[] generateSimpleArabicPdf() throws IOException {
    String htmlContent = "<html>" +
            "<head>" +
            "<meta charset=\"UTF-8\">" +
            "<style>" +
            "body { direction: rtl; font-family: 'Noto Naskh Arabic', Arial, sans-serif; }" +
            "</style>" +
            "</head>" +
            "<body>" +
            "<p>مرحبا بكم في قائمة الروابط</p>" +
            "</body>" +
            "</html>";

    return convertHtmlToPdf(htmlContent, "AR");
}

}
