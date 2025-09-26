package cloudflight.integra.backend.controller;

import cloudflight.integra.backend.service.SampleDataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/sample-data")
@CrossOrigin(origins = "http://localhost:4200")
public class SampleDataController {

    @Autowired
    private SampleDataService sampleDataService;

    @PostMapping("/generate")
    public ResponseEntity<Map<String, String>> generateSampleData() {
        try {
            sampleDataService.generateAllSampleData();
            Map<String, String> response = new HashMap<>();
            response.put("message", "Sample data generated successfully!");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, String> response = new HashMap<>();
            response.put("message", "Error generating sample data: " + e.getMessage());
            return ResponseEntity.status(500).body(response);
        }
    }
}

