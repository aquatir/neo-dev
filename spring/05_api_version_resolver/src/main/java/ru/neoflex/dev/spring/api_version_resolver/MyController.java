package ru.neoflex.dev.spring.api_version_resolver;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class MyController {

    @RequestMapping("/url_content_type")
    public ResponseEntity<String> url1_accept() {
        return ResponseEntity.ok("url1_accept");
    }

    @RequestMapping("/url_content_type")
    public ResponseEntity<String> url2_accept() {
        return ResponseEntity.ok("url2_accept");
    }


    @RequestMapping("/url_header")
    public ResponseEntity<String> url1_header() {
        return ResponseEntity.ok("url1_header");
    }

    @RequestMapping("/url_header")
    public ResponseEntity<String> url2_header() {
        return ResponseEntity.ok("url2_header");
    }
}
