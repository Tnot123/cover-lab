package com.example.domxss;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class DomXssController {

    /**
     * Reflected DOM XSS lab page.
     * - GET /domxss returns a page with a form and JS that reads `msg` from the query string
     *   and writes it into the DOM using innerHTML (deliberately vulnerable).
     */
    @GetMapping("/domxss")
    @ResponseBody
    public String domXssPage() {
        return "<!doctype html>\n" +
                "<html lang=\"en\">\n" +
                "<head>\n" +
                "  <meta charset=\"utf-8\">\n" +
                "  <meta http-equiv=\"X-UA-Compatible\" content=\"IE=edge\" />\n" +
                "  <meta name=\"viewport\" content=\"width=device-width, initial-scale=1\" />\n" +
                "  <title>Reflected DOM XSS Lab</title>\n" +
                "  <style>\n" +
                "    body { font-family: Arial, Helvetica, sans-serif; margin: 24px; }\n" +
                "    input[type=text]{ width: 60%; padding: 6px; }\n" +
                "    button { padding: 6px 10px; }\n" +
                "    #output { margin-top: 18px; padding: 10px; border: 1px solid #ddd; }\n" +
                "  </style>\n" +
                "</head>\n" +
                "<body>\n" +
                "\n" +
                "  <form action=\"/domxss\" method=\"get\">\n" +
                "    <label for=\"msg\">Message:</label>\n" +
                "    <input type=\"text\" id=\"msg\" name=\"msg\" />\n" +
                "    <button type=\"submit\">Submit</button>\n" +
                "  </form>\n" +
                "\n" +
                "  <div id=\"output\">(no message yet)</div>\n" +
                "\n" +
                "  <script>\n" +
                "    // Read parameter 'msg' from the URL\n" +
                "    const params = new URLSearchParams(window.location.search);\n" +
                "    const msg = params.get('msg');\n" +
                "\n" +
                "    // ===== VULNERABLE REFLECTION (deliberate) =====\n" +
                "    // The value is inserted using innerHTML without any encoding or sanitization.\n" +
                "    // This is a Reflected DOM XSS vector.\n" +
                "    if (msg) {\n" +
                "      document.getElementById('output').innerHTML = 'You submitted: ' + msg;\n" +
                "    }\n" +
                "\n" +
                "    // Helpful: echo the raw query string in console for debugging\n" +
                "    console.log('query=', window.location.search);\n" +
                "  </script>\n" +
                "</body>\n" +
                "</html>";
    }
}
