package com.github.jacdevv.paymentreconcillation.config;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = SecurityConfigTest.TestController.class)
@Import({SecurityConfig.class, SecurityConfigTest.TestController.class})
class SecurityConfigTest {

    @Autowired
    private MockMvc mockMvc;

    @RestController
    static class TestController {
        @GetMapping("/api/public/ping")
        public String publicPing() {
            return "pong";
        }

        @GetMapping("/api/reconciliation/summary")
        public String protectedSummary() {
            return "summary-data";
        }
    }

    @Test
    @DisplayName("Public endpoints under /api/public/** should permit unauthenticated access")
    void publicEndpoints_shouldAllowAnonymous() throws Exception {
        mockMvc.perform(get("/api/public/ping"))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("Protected endpoints should reject unauthenticated requests with 401 Unauthorized")
    void protectedEndpoints_shouldDenyAnonymous() throws Exception {
        mockMvc.perform(get("/api/reconciliation/summary"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("Protected endpoints should allow requests with a valid JWT token")
    void protectedEndpoints_shouldAllowAuthenticatedJwt() throws Exception {
        mockMvc.perform(get("/api/reconciliation/summary").with(jwt()))
                .andExpect(status().isOk());
    }
}
