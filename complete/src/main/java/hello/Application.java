package hello;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import net.objecthunter.exp4j.ExpressionBuilder;

@SpringBootApplication
@RestController
public class Application {

    @RequestMapping("/")
    public String home() {
        return "<html><body>" +
               "<h2>Мій Калькулятор</h2>" +
               "<form action='/calc' method='GET'>" +
               "<input type='text' name='expr' placeholder='Писати тутаа' required/>" +
               "<button type='submit'>Порахувати</button>" +
               "</form>" +
               "</body></html>";
    }

    @RequestMapping("/calc")
    public String calculate(@RequestParam String expr) {
        try {
            double result = new ExpressionBuilder(expr).build().evaluate();
            return "<html><body>" +
                   "<h2>Вираз: " + expr + "</h2>" +
                   "<h2>Результат: " + result + "</h2>" +
                   "<br><a href='/'>Повернутися назад</a>" +
                   "</body></html>";
        } catch (Exception e) {
            return "<html><body><h2> Помилка((  </h2><a href='/'>Назад</a></body></html>";
        }
    }

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
        java.util.TimeZone.setDefault(java.util.TimeZone.getTimeZone("UTC"));
    }
}