package cloudflight.integra.backend.controller;

import cloudflight.integra.backend.service.SampleDataService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.containsString;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(SampleDataController.class)
public class SampleDataControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private SampleDataService sampleDataService;

    @Test
    void generateSampleData_success() throws Exception {
        doNothing().when(sampleDataService)
                .generateAllSampleData();

        mockMvc.perform(post("/api/sample-data/generate"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value(containsString("Sample data generated successfully")));

        verify(sampleDataService, times(1)).generateAllSampleData();
    }

    @Test
    void generateSampleData_serviceThrowsException_returnsInternalServerError() throws Exception {
        doThrow(new RuntimeException("Database connection failed"))
                .when(sampleDataService)
                .generateAllSampleData();

        mockMvc.perform(post("/api/sample-data/generate"))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.message").value(containsString("Error generating sample data")));

        verify(sampleDataService, times(1)).generateAllSampleData();
    }

    @Test
    void generateSampleData_serviceThrowsIllegalArgumentException_returnsInternalServerError() throws Exception {
        doThrow(new IllegalArgumentException("Invalid data"))
                .when(sampleDataService)
                .generateAllSampleData();

        mockMvc.perform(post("/api/sample-data/generate"))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.message").value(containsString("Invalid data")));

        verify(sampleDataService, times(1)).generateAllSampleData();
    }

    @Test
    void generateSampleData_endpoint_acceptsOnlyPost() throws Exception {
        mockMvc.perform(post("/api/sample-data/generate"))
                .andExpect(status().isOk());
    }
}

