package com.partnerizt;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource(properties = {
        "spring.datasource.url=jdbc:h2:mem:testdb;DB_CLOSE_DELAY=-1;MODE=PostgreSQL",
        "spring.datasource.driver-class-name=org.h2.Driver",
        "spring.datasource.username=sa",
        "spring.datasource.password=",
        "spring.jpa.hibernate.ddl-auto=create-drop"
})
public class PartneriztApplicationTests {

    @Autowired
    private MockMvc mockMvc;

    @Test
    @DisplayName("Context loads and controllers are accessible")
    void contextLoads() {
    }

    @Test
    @DisplayName("GET /api/v1/users/me returns default seeded user")
    void testGetCurrentUser() throws Exception {
        mockMvc.perform(get("/api/v1/users/me"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.username").value("nature_scout"));
    }

    @Test
    @DisplayName("GET /api/v1/companions returns seeded domain experts Birdo, Flora, Atlas, Munch, Nova")
    void testGetCompanions() throws Exception {
        mockMvc.perform(get("/api/v1/companions"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.length()").value(5));
    }

    @Test
    @DisplayName("GET /api/v1/quests/daily returns daily quests")
    void testGetDailyQuests() throws Exception {
        mockMvc.perform(get("/api/v1/quests/daily"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));
    }

    @Test
    @DisplayName("POST /api/v1/chat/flora/send returns domain expert response")
    void testChatWithCompanion() throws Exception {
        mockMvc.perform(post("/api/v1/chat/flora/send")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"message\":\"How do trees pull water up to leaves?\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));
    }

    @Test
    @DisplayName("POST /api/v1/discoveries/identify runs full AI discovery pipeline")
    void testIdentifyOutdoorPhoto() throws Exception {
        mockMvc.perform(post("/api/v1/discoveries/identify")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"photoUrl\":\"https://images.unsplash.com/photo-1528183429752-a97d0bf99b5a\",\"companionId\":\"flora\",\"userQuery\":\"Oak leaf on ground\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.identified").exists())
                .andExpect(jsonPath("$.data.sources").isArray());
    }
}
