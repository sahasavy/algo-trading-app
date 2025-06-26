package com.algo.trading.ml.controller;

import org.junit.jupiter.api.Test;
import org.springframework.batch.test.context.SpringBatchTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBatchTest
@SpringBootTest(properties = {
        // Use in-memory H2 for tests
        "spring.datasource.url=jdbc:h2:mem:testdb;DB_CLOSE_DELAY=-1",
        "spring.datasource.driver-class-name=org.h2.Driver",
        "spring.datasource.username=sa",
        "spring.datasource.password=",

        // Auto-create the Batch tables
        "spring.batch.jdbc.initialize-schema=always",

        // Don’t run ML jobs on startup
        "spring.batch.job.enabled=false",

        // Skip Liquibase in tests
        "spring.liquibase.enabled=false"
})
@AutoConfigureMockMvc
//@ActiveProfiles("test")    // ← pick up application-test.yml
public class MlControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void whenPostTrain_thenAccepted() throws Exception {
        mockMvc.perform(post("/ml/train")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isAccepted())
                .andExpect(content().string("ML training job started"));
    }
}
