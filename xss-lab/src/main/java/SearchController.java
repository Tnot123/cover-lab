package com.example.xsslab;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

@RestController
public class SearchController {

    @GetMapping(value = "/search", produces = MediaType.TEXT_HTML_VALUE)
    public String search(@RequestParam(name = "term", required = false, defaultValue = "") String term) {
        return "<!doctype html>\n" +
                "<html lang=\"en\">\n" +
                "<head>\n" +
                "  <meta charset=\"utf-8\" />\n" +
                "  <title>Search - Vulnerable Lab</title>\n" +
                "</head>\n" +
                "<body>\n" +
                "  <h1>Search results</h1>\n" +
                "  <p>You searched for: " + term + "</p>\n" +
                "  <a href=\"/\">Back to search</a>\n" +
                "</body>\n" +
                "</html>";
    }
}
