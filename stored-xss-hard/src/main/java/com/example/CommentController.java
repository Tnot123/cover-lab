package com.example.dvwahigh;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.annotation.PostConstruct;
import java.util.List;
import java.util.Map;

@Controller
public class CommentController {

    @Autowired
    private JdbcTemplate jdbc;

    @PostConstruct
    public void init() {
        // Tạo bảng nếu chưa có
        jdbc.execute("CREATE TABLE IF NOT EXISTS comments (" +
                "id INT AUTO_INCREMENT PRIMARY KEY, " +
                "author VARCHAR(200), " +
                "message VARCHAR(2000))");
        Integer c = jdbc.queryForObject("SELECT COUNT(*) FROM comments", Integer.class);
        if (c != null && c == 0) {
            jdbc.update("INSERT INTO comments (author, message) VALUES (?, ?)",
                    "Admin", "Welcome to Stored XSS demo.");
        }
    }

    @GetMapping("/")
    @ResponseBody
    public String home() {
        List<Map<String, Object>> rows = jdbc.queryForList("SELECT id, author, message FROM comments ORDER BY id DESC");
        StringBuilder sb = new StringBuilder();
        sb.append("<!doctype html><html><head><meta charset='utf-8'><title> Stored XSS</title>");
        sb.append("<style>body{font-family:Arial,Helvetica,sans-serif;margin:20px} table{border-collapse:collapse} td,th{border:1px solid #ccc;padding:6px}</style>");
        sb.append("</head><body>");
        sb.append("<h1>DVWA High - Stored XSS (demo)</h1>");

        // Form
        sb.append("<form method='post' action='/comment'>")
                .append("Name: <input type='text' name='author' maxlength='200'><br><br>")
                .append("Message:<br><textarea name='message' rows='5' cols='60'></textarea><br><br>")
                .append("<button type='submit'>Post comment</button>")
                .append("</form><hr>");

        sb.append("<h2>All comments</h2>");
        sb.append("<table><tr><th>id</th><th>author</th><th>message</th></tr>");
        for (Map<String,Object> r : rows) {
            sb.append("<tr>");
            sb.append("<td>").append(r.get("id")).append("</td>");
            sb.append("<td>").append(r.get("author")).append("</td>");
            sb.append("<td class='render-me'>").append(r.get("message")).append("</td>");
            sb.append("</tr>");
        }
        sb.append("</table>");

        // Vulnerable client-side decoder: decodes entity-encoded text into HTML (THIS IS THE VULNERABLE PART)
        sb.append("<script>\n" +
                "  document.addEventListener('DOMContentLoaded', function() {\n" +
                "    var cells = document.querySelectorAll('td.render-me');\n" +
                "    cells.forEach(function(c) {\n" +
                "      try {\n" +
                "        // INSECURE: move textual content (may contain &lt;...&gt;) into innerHTML -> decoding entities\n" +
                "        c.innerHTML = c.innerText;\n" +
                "      } catch (e) { console.error(e); }\n" +
                "    });\n" +
                "  });\n" +
                "</script>");

        sb.append("</body></html>");
        return sb.toString();
    }

    @PostMapping("/comment")
    public String postComment(@RequestParam String author, @RequestParam String message) {
        String safeAuthor = escapeHigh(author);
        String safeMessage = escapeHigh(message);

        jdbc.update("INSERT INTO comments (author, message) VALUES (?, ?)", safeAuthor, safeMessage);
        // redirect back to home
        return "redirect:/";
    }

    private static String escapeHigh(String s) {
        if (s == null) return "";
        // Neutralize direct <script or </script (case-insensitive)
        s = s.replaceAll("(?i)<\\s*script", "&lt;script-blocked");
        s = s.replaceAll("(?i)</\\s*script", "&lt;/script-blocked");
        // Neutralize some other tag names that are commonly used
        s = s.replaceAll("(?i)<\\s*iframe", "&lt;iframe-blocked");
        s = s.replaceAll("(?i)<\\s*svg", "&lt;svg-blocked");
        // Escape remaining < and >
        s = s.replace("<", "&lt;").replace(">", "&gt;");
        // Do NOT escape '&' intentionally (so &lt; stays &lt;)
        return s;
    }
}
