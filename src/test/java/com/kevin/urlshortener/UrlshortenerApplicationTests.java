package com.kevin.urlshortener;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kevin.urlshortener.dto.CreateRequest;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import java.io.StringWriter;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = UrlshortenerApplication.class)
@AutoConfigureMockMvc
class UrlshortenerApplicationTests {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void create() throws Exception {
//        TODO Abstract json
        CreateRequest createRequest = new CreateRequest("www.google.com");
        ObjectMapper objectMapper = new ObjectMapper();
        StringWriter sw = new StringWriter();
        objectMapper.writeValue(sw, createRequest);

        //TODO abstract perform
        mockMvc.perform(
                post("/tinyurl/create")
                        .header("Content-Type", "application/json")
                        .content(sw.toString()))
                .andExpect(status().is(200))
                .andExpect(content().string(containsString("tinyurl/get/")));

    }

}
