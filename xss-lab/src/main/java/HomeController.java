package com.example.xsslab;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class HomeController {

    // Trang chủ có form tìm kiếm
    @GetMapping("/")
    @ResponseBody
    public String home() {
        return "<!doctype html>" +
                "<html lang='en'>" +
                "<head>" +
                "  <meta charset='utf-8'>" +
                "  <title>Search</title>" +
                "</head>" +
                "<body>" +
                "  <h1>Welcome Lab XSS</h1>" +
                "  <form action='/search' method='get'>" +
                "    <label for='term'>Search:</label>" +
                "    <input type='text' id='term' name='term' placeholder='Type something...'>" +
                "    <button type='submit'>Go</button>" +
                "  </form>" +
                "</body>" +
                "</html>";
    }

    // Trang kết quả tìm kiếm - deliberately vulnerable
    @GetMapping("/search")
    @ResponseBody
    public String search(@RequestParam(name = "term", required = false, defaultValue = "") String term) {
        return "<!doctype html>" +
                "<html lang='en'>" +
                "<head>" +
                "  <meta charset='utf-8'>" +
                "  <title>Search - Vulnerable Lab</title>" +
                "</head>" +
                "<body>" +
                "  <h1>Search results</h1>" +
                "  <p>You searched for: " + term + "</p>" + // Không encode -> vulnerable
                "  <a href='/'>Back to search</a>" +
                "</body>" +
                "</html>";
    }
}
