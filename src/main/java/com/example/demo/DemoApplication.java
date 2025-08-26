package com.example.demo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;

@SpringBootApplication
public class DemoApplication {
    public static void main(String[] args) {
        SpringApplication.run(DemoApplication.class, args);
    }
}

@RestController
class HelloController {
    @GetMapping("/")
    public ResponseEntity<String> hello() {
        String apiUrl = "https://api.thecatapi.com/v1/images/search";
        RestTemplate restTemplate = new RestTemplate();

        try {
            List<Map<String, Object>> response = restTemplate.getForObject(apiUrl, List.class);
            Map<String, Object> catData = response.get(0);
            String imageUrl = catData.get("url").toString();

            String html = """
                <html>
                  <head><title>Random Cat 🐱</title></head>
                  <body style="text-align:center;">
                    <h1>Imagen aleatoria de gato 🐱 - Test CI/CD</h1>
                    <img src="%s" alt="Cat Image" style="max-width: 600px;"/>
                    <p>Powered by <a href="https://thecatapi.com/" target="_blank">The Cat API</a></p>
                  </body>
                </html>
                """.formatted(imageUrl);

            return ResponseEntity.ok(html);

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error al obtener imagen de gato");
        }
    }

}
