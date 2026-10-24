package hello;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.Statement;

@RestController
public class Lab3Part1Controller {
    private final String DB_URL = "jdbc:postgresql://localhost:5432/mydatabase";
    private final String USER = "myuser";
    private final String PASS = "secret";

    @GetMapping("/lab3/unsafe")
    public String unsafeQuery(@RequestParam(required = false) String query) {
        StringBuilder html = new StringBuilder();
        html.append("<html><body style='font-family: Arial;'><h2>Вразливий JDBC</h2>");
        html.append("<form action='/lab3/unsafe' method='GET'>");
        html.append("<textarea name='query' rows='5' cols='60' placeholder='Тут введіть SQL запит...'>")
            .append(query != null ? query : "").append("</textarea><br><br>");
        html.append("<button type='submit'>Виконати</button>");
        html.append("</form><hr>");

        if (query != null && !query.trim().isEmpty()) {
            try (Connection conn = DriverManager.getConnection(DB_URL, USER, PASS);
                 Statement stmt = conn.createStatement()) {
                boolean isResultSet = stmt.execute(query);
                
                if (isResultSet) {
                    ResultSet rs = stmt.getResultSet();
                    ResultSetMetaData metaData = rs.getMetaData();
                    int columnCount = metaData.getColumnCount();

                    html.append("<table border='1' cellpadding='5' style='border-collapse: collapse;'><tr>");
                    for (int i = 1; i <= columnCount; i++) {
                        html.append("<th style='background-color: black;'>").append(metaData.getColumnName(i)).append("</th>");
                    }
                    html.append("</tr>");

                   
                    while (rs.next()) {  // вивід даних
                        html.append("<tr>");
                        for (int i = 1; i <= columnCount; i++) {
                            html.append("<td>").append(rs.getString(i)).append("</td>");
                        }
                        html.append("</tr>");
                    }
                    html.append("</table>");
                } else {
                    html.append("<p style='color: purple;'>Запит виконано успішноооо! ").append(stmt.getUpdateCount()).append("</p>");
                }
            } catch (Exception e) {
                html.append("<p style='color:red;'><b>Помилка БД:</b> ").append(e.getMessage()).append("</p>");
            }
        }
        html.append("</body></html>");
        return html.toString();
    }
}