package com.github.syakimovich.chessserver;

import com.github.syakimovich.chessserver.consts.GameStatus;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.json.JsonParser;
import org.springframework.boot.json.JsonParserFactory;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@AutoConfigureMockMvc
@Sql(statements = "truncate table users cascade;")
public class ChessServerMockMvcTests {

    private final String USERNAME = "user1";
    private final String PASSWORD = "pass1";

    @Autowired
    private MockMvc mockMvc;

    @Test
    @WithMockUser(value = USERNAME, password = PASSWORD)
    void createGame() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post("/signup")
                        .param("username", USERNAME)
                        .param("password", PASSWORD))
                .andExpect(MockMvcResultMatchers.status().is3xxRedirection())
                .andExpect(MockMvcResultMatchers.view().name("redirect:/login"));
        MvcResult createResult = mockMvc.perform(MockMvcRequestBuilders.post("/game/create").contentType(MediaType.APPLICATION_JSON).content("{\"creator\": \"" + USERNAME + "\", \"creatorWhite\": \"true\"}"))
                .andExpect(MockMvcResultMatchers.status().isOk()).andReturn();
        long gameId = Long.parseLong(createResult.getResponse().getContentAsString());
        MvcResult getGameResult = mockMvc.perform(MockMvcRequestBuilders.get("/game/" + gameId))
                .andExpect(MockMvcResultMatchers.status().isOk()).andReturn();
        JsonParser jsonParser = JsonParserFactory.getJsonParser();
        Map<String, Object> jsonMap = jsonParser.parseMap(getGameResult.getResponse().getContentAsString());
        assertEquals(USERNAME, jsonMap.get("creator"));
        assertTrue((Boolean) jsonMap.get("creatorWhite"));
        assertEquals(GameStatus.BLACK_TO_JOIN.toString(), jsonMap.get("status"));
    }
}
