package hello;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@RestController
public class Lab3Part2Controller {

    private final String DB_URL = "jdbc:postgresql://localhost:5432/mydatabase";
    private final String USER = "myuser";
    private final String PASS = "secret";

    @GetMapping("/lab3/safe")
    public String safeCommand(@RequestParam(required = false) String command) {
        StringBuilder html = new StringBuilder();
        html.append("<html><body style='font-family: Arial;'><h2>Захищений JDBC </h2>");
        html.append("<form action='/lab3/safe' method='GET'>");
        html.append("<input type='text' name='command' style='width: 600px;' placeholder=\"Наприклад: insert city(id='2', name='Kyiv');\" value='")
            .append(command != null ? command : "").append("'/><br><br>");
        html.append("<button type='submit'> Тик тик </button>");
        html.append("</form><hr>");

        if (command != null && !command.trim().isEmpty()) {
            try (Connection conn = DriverManager.getConnection(DB_URL, USER, PASS)) {
                
                if (command.toLowerCase().startsWith("insert")) {
                    Pattern pattern = Pattern.compile("insert\\s+(\\w+)\\s*\\((.+)\\);?", Pattern.CASE_INSENSITIVE);
                    Matcher matcher = pattern.matcher(command);
                    
                    if (matcher.find()) {
                        String table = matcher.group(1); 
                        String data = matcher.group(2);  
                        
                        String[] pairs = data.split(",");
                        StringBuilder cols = new StringBuilder();
                        StringBuilder placeholders = new StringBuilder();
                        
                        for (int i = 0; i < pairs.length; i++) {
                            cols.append(pairs[i].split("=")[0].trim());
                            placeholders.append("?"); 
                            if (i < pairs.length - 1) {
                                cols.append(", ");
                                placeholders.append(", ");
                            }
                        }
                        
                        String sql = "INSERT INTO " + table + " (" + cols.toString() + ") VALUES (" + placeholders.toString() + ")";
                        
                        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                            for (int i = 0; i < pairs.length; i++) {
                                String val = pairs[i].split("=")[1].trim().replace("'", "");
                                if (val.matches("\\d+")) {
                                    pstmt.setInt(i + 1, Integer.parseInt(val));
                                } else {
                                    pstmt.setString(i + 1, val);
                                }
                            }

                            int rows = pstmt.executeUpdate();
                            html.append("<p style='color:green;'>Успішно додано запис! Змінено рядків: ").append(rows).append("</p>");
                        }
                    } else {
                        html.append("<p style='color:red;'>Невірний формат команди... insert.</p>");
                    }
                } 
            
                else if (command.toLowerCase().startsWith("delete")) {
                    Pattern pattern = Pattern.compile("delete\\s+(\\w+)\\s*\\((.+)\\);?", Pattern.CASE_INSENSITIVE);
                    Matcher matcher = pattern.matcher(command);
                    
                    if (matcher.find()) {
                        String table = matcher.group(1);
                        String condition = matcher.group(2); 
                        
                        String[] kv = condition.split("=");
                        String col = kv[0].trim();
                        String val = kv[1].trim().replace("'", "");
                        
                        String sql = "DELETE FROM " + table + " WHERE " + col + " = ?";
                        
                        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                            if (val.matches("\\d+")) {
                                pstmt.setInt(1, Integer.parseInt(val));
                            } else {
                                pstmt.setString(1, val);
                            }
                            int rows = pstmt.executeUpdate();
                            html.append("<p style='color:green;'> Все успішно видалено!  ").append(rows).append("</p>");
                        }
                    } else {
                        html.append("<p style='color:red;'>Невірний формат команди...</p>");
                    }
                } else {
                    html.append("<p style='color:red;'>Команда не підтримується. Або insert або delete... </p>");
                }
            } catch (Exception e) {
                html.append("<p style='color:red;'><b> Тут помилка БД:</b> ").append(e.getMessage()).append("</p>");
            }
        }
        html.append("</body></html>");
        return html.toString();
    }
}