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

import org.codecritters.code_critters.application.service.WebScrapingQService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

@RestController
@RequestMapping("/api/webscraping/queues")
public class WebScrapingQController {

    private final WebScrapingQService webScrapingQService;

    @Autowired
    public WebScrapingQController(WebScrapingQService webScrapingQService) {
        this.webScrapingQService = webScrapingQService;
    }

    /**
     * Endpoint to scrape and return the Queue content as HTML in the specified language.
     * 
     * Example: GET /api/webscraping/queues/scrape?lang=DE
     *
     * @param lang The language code (default is EN).
     * @return The scraped content in HTML format.
     */
    @GetMapping("/scrape")
    public ResponseEntity<String> scrapeQueuesPage(
            @RequestParam(value = "lang", defaultValue = "EN") String lang) {
        try {
            String scrapedContent = webScrapingQService.getContentInLanguage(lang);
            return ResponseEntity.ok(scrapedContent);
        } catch (IOException e) {
            return ResponseEntity.status(500).body("Error occurred while scraping: " + e.getMessage());
        }
    }

    /**
     * Endpoint to download the scraped Queue content as a PDF in the specified language.
     * 
     * Example: GET /api/webscraping/queues/download-pdf?lang=DE
     *
     * @param lang The language code (default is EN).
     * @return The PDF file as a downloadable attachment.
     */
  // Endpoint to download the scraped content as a PDF in the specified language
    @GetMapping("/download-pdf")
    public ResponseEntity<Resource> downloadScrapedContentAsPdf(@RequestParam(value = "lang", defaultValue = "EN") String lang) {
        try {
            String filename = "DataStructure" + "Queue" + ".pdf";
            Resource resource;

            if (lang.equalsIgnoreCase("AR")) {
                // Serve the pre-generated Arabic PDF
                Path pdfPath = Paths.get("src/main/resources/pdf/queue (1).pdf");
                resource = new UrlResource(pdfPath.toUri());

                if (!resource.exists()) {
                    throw new IOException("Arabic PDF file not found.");
                }
            } else {
                // Generate the PDF dynamically
                byte[] pdfContent = webScrapingQService.getScrapedContentAsPdf(lang);
                resource = new ByteArrayResource(pdfContent);
            }

            // Set headers to force download
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_PDF);
            headers.setContentDispositionFormData("attachment", filename);

            // Return the PDF as a downloadable file
            return ResponseEntity.ok()
                    .headers(headers)
                    .body(resource);

        } catch (IOException e) {
            return ResponseEntity.status(400).body(null);
        }
    }
}
