package com.example.xsslab;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.annotation.PostConstruct;
import java.util.List;
import java.util.Map;

@Controller
public class LinkController {

    @Autowired
    private JdbcTemplate jdbc;

    // Tạo bảng nếu cần (optional, schema.sql cũng có thể làm việc)
    @PostConstruct
    public void init() {
        jdbc.execute("CREATE TABLE IF NOT EXISTS links (" +
                "id INT AUTO_INCREMENT PRIMARY KEY, " +
                "description VARCHAR(255), " +
                "url VARCHAR(1024))");
        // nếu bảng trống, chèn vài link mẫu
        Integer count = jdbc.queryForObject("SELECT COUNT(*) FROM links", Integer.class);
        if (count != null && count == 0) {
            jdbc.update("INSERT INTO links (description, url) VALUES (?, ?)",
                    "Google", "https://www.google.com");
            jdbc.update("INSERT INTO links (description, url) VALUES (?, ?)",
                    "Example", "https://example.com");
        }
    }

    // Hiển thị form submit + danh sách links
    @GetMapping("/")
    @ResponseBody
    public String home() {
        return renderPage(null, null);
    }

    // Xử lý submit link (store)
    @PostMapping("/submit")
    @ResponseBody
    public String submit(@RequestParam String description, @RequestParam String url) {
        // LƯU RAW vào DB: deliberate stored XSS lab
        jdbc.update("INSERT INTO links (description, url) VALUES (?, ?)", description, url);
        return renderPage("Link saved.", null);
    }

    // Render trang: hiện form và danh sách links
    private String renderPage(String notice, String error) {
        StringBuilder html = new StringBuilder();
        html.append("<!doctype html><html lang='en'><head><meta charset='utf-8'><title>Stored XSS (href) Lab</title></head><body>");
        html.append("<h1>Stored XSS)</h1>");

        if (notice != null) html.append("<p style='color:green;'>").append(escapeHtml(notice)).append("</p>");
        if (error != null) html.append("<p style='color:red;'>").append(escapeHtml(error)).append("</p>");

        // Form
        html.append("<h2>Submit a link</h2>");
        html.append("<form method='post' action='/submit'>")
                .append("Description: <input type='text' name='description' required maxlength='200'><br>")
                .append("URL: <input type='text' name='url' required maxlength='1000' placeholder='e.g. https://example.com or javascript:alert(1)'><br>")
                .append("<button type='submit'>Save link</button>")
                .append("</form>");

        // List links
        html.append("<h2>Stored links</h2>");
        List<Map<String, Object>> rows = jdbc.queryForList("SELECT id, description, url FROM links ORDER BY id DESC");
        html.append("<ul>");
        for (Map<String, Object> r : rows) {
            String desc = r.get("description") == null ? "" : r.get("description").toString();
            String url = r.get("url") == null ? "" : r.get("url").toString();

            // IMPORTANT: encode only double quotes in href value, intentionally NOT escaping other chars
            String hrefValue = encodeDoubleQuotes(url);

            // Escape description for safe display
            String safeDesc = escapeHtml(desc);

            // Render anchor — notice description is safe-escaped; href only has " -> &quot;
            html.append("<li>")
                    .append("<a href=\"").append(hrefValue).append("\">")
                    .append(safeDesc)
                    .append("</a>")
                    .append(" &nbsp; (<code>").append(escapeHtml(url)).append("</code>)")
                    .append("</li>");
        }
        html.append("</ul>");

        html.append("</body></html>");
        return html.toString();
    }

    // Replace only double quotes with &quot;
    private static String encodeDoubleQuotes(String s) {
        if (s == null) return "";
        return s.replace("\"", "&quot;");
    }

    // Minimal HTML escape for general text (to avoid other display vectors)
    private static String escapeHtml(String s) {
        if (s == null) return "";
        return s.replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("\"", "&quot;")
                .replace("'", "&#x27;");
    }
}
