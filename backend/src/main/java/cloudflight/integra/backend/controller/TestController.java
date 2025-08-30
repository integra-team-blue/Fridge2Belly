package cloudflight.integra.backend.controller;


import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class TestController {

    @GetMapping("/test")
    public List<String> testEndpoint() {
        return List.of("hello", "world");
    }
}
