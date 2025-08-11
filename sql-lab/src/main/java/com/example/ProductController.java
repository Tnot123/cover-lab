package com.example.sqllab;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Controller
public class ProductController {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    // Trang nhập tìm kiếm
    @GetMapping("/products")
    @ResponseBody
    public String searchPage(@RequestParam(value = "category", required = false) String category) {
        StringBuilder html = new StringBuilder();
        html.append("<!DOCTYPE html>")
                .append("<html><head><meta charset='UTF-8'><title>Products</title></head><body>");

        if (category == null) {
            // Form nhập
            html.append("<h1>Search Products</h1>")
                    .append("<form action='/products' method='get'>")
                    .append("<input type='text' name='category' placeholder='Enter category'>")
                    .append("<button type='submit'>Search</button>")
                    .append("</form>");
        } else {
            // SQL Injection intentionally left vulnerable
            String sql = "SELECT * FROM products WHERE category = '" + category + "'";
            List<Map<String, Object>> products = jdbcTemplate.queryForList(sql);

            html.append("<h1>Products for category: ").append(category).append("</h1>");
            html.append("<table border='1'><tr><th>id</th><th>name</th><th>category</th><th>price</th></tr>");

            for (Map<String, Object> product : products) {
                html.append("<tr>")
                        .append("<td>").append(product.get("id")).append("</td>")
                        .append("<td>").append(product.get("name")).append("</td>")
                        .append("<td>").append(product.get("category")).append("</td>")
                        .append("<td>").append(product.get("price")).append("</td>")
                        .append("</tr>");
            }

            html.append("</table>")
                    .append("<br><a href='/products'>Back</a>");
        }

        html.append("</body></html>");
        return html.toString();
    }
}
