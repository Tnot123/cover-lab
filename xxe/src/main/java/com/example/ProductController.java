package com.example.xxeprod;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import javax.annotation.PostConstruct;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;

import java.nio.charset.StandardCharsets;
import java.io.File;
import java.io.FileOutputStream;


@RestController
public class ProductController {



    @GetMapping(path = "/product/{id}", produces = MediaType.TEXT_HTML_VALUE)
    public String productPage(@PathVariable("id") String id) {
        String html =
                "<!doctype html>\n" +
                        "<html><head><meta charset='utf-8'><title>Product " + escapeHtml(id) + "</title></head><body>\n" +
                        "  <h1>Product ID: " + escapeHtml(id) + "</h1>\n" +
                        "  <button id='checkBtn'>Check stock</button>\n" +
                        "  <pre id='result' style='background:#f4f4f4;padding:10px;margin-top:10px;'></pre>\n" +
                        "\n" +
                        "  <script>\n" +
                        "    document.getElementById('checkBtn').addEventListener('click', function() {\n" +
                        "      var xml = '<?xml version=\"1.0\" encoding=\"UTF-8\"?>\\n' +\n" +
                        "                '<stockCheck>\\n' +\n" +
                        "                '  <productId>" + id + "</productId>\\n' +\n" +
                        "                '  <storeId>1</storeId>\\n' +\n" +
                        "                '</stockCheck>';\n" +
                        "      fetch('/stock', {\n" +
                        "        method: 'POST',\n" +
                        "        headers: { 'Content-Type': 'application/xml' },\n" +
                        "        body: xml,\n" +
                        "        credentials: 'same-origin'\n" +
                        "      }).then(function(resp) {\n" +
                        "        return resp.text();\n" +
                        "      }).then(function(text) {\n" +
                        "        document.getElementById('result').textContent = text;\n" +
                        "      }).catch(function(err){ document.getElementById('result').textContent = 'Error: '+err; });\n" +
                        "    });\n" +
                        "  </script>\n" +
                        "</body></html>";
        return html;
    }

    @PostMapping(path = "/stock", consumes = MediaType.APPLICATION_XML_VALUE, produces = MediaType.TEXT_PLAIN_VALUE)
    public String checkStock(@RequestBody String xml) throws Exception {

        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();

        try {
            dbf.setFeature("http://apache.org/xml/features/disallow-doctype-decl", false);
        } catch (Exception ignored) {}
        try {
            dbf.setFeature("http://xml.org/sax/features/external-general-entities", true);
        } catch (Exception ignored) {}
        try {
            dbf.setFeature("http://xml.org/sax/features/external-parameter-entities", true);
        } catch (Exception ignored) {}
        dbf.setExpandEntityReferences(true);

        DocumentBuilder db = dbf.newDocumentBuilder();
        Document doc = db.parse(new java.io.ByteArrayInputStream(xml.getBytes(StandardCharsets.UTF_8)));

        String productIdText = "";
        try {
            productIdText = doc.getDocumentElement().getElementsByTagName("productId")
                    .item(0).getTextContent();
        } catch (Exception e) {
            productIdText = "";
        }

        return "Invalid product ID: " + productIdText;
    }

    private static String escapeHtml(String s) {
        if (s == null) return "";
        return s.replace("&", "&amp;").replace("<","&lt;").replace(">", "&gt;");
    }
}
