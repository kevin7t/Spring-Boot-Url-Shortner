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
import org.springframework.test.web.servlet.ResultActions;

import java.io.IOException;
import java.io.StringWriter;

import static org.hamcrest.Matchers.containsString;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
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
    public void create_success() throws Exception {
        String request = requestToJson("www.google.com");

        mockMvc.perform(
                post("/tinyurl/create")
                        .header("Content-Type", "application/json")
                        .content(request))
                .andExpect(status().is(200))
                .andExpect(content().string(containsString("http://127.0.0.1:8080/tinyurl/")));

    }

    @Test
    public void getAfterCreate_success() throws Exception {
        final String REQUEST_URL = "www.google.com";

        String request = requestToJson(REQUEST_URL);
        //Result of creation of shortener
        ResultActions result = mockMvc.perform(
                post("/tinyurl/create")
                        .header("Content-Type", "application/json")
                        .content(request))
                .andExpect(status().is(200))
                .andExpect(content().string(containsString("http://127.0.0.1:8080/tinyurl/")));


        String shortUrl = result.andReturn().getResponse().getContentAsString();
        ResultActions getResult = mockMvc.perform(
                get(shortUrl))
                .andExpect(status().is(301));

        //Assert response code is 301 - Moved permanently
        //Assert Location header is the redirected webpage therefore causing browser redirect
        assertEquals("", getResult.andReturn().getResponse().getContentAsString());
        //Asserting with Http:// as the code will add it, if not exist
        assertEquals("http://" + REQUEST_URL, getResult.andReturn().getResponse().getHeader("Location"));
    }

    //TODO Write QR Code test
    //Test - Test if buffered image can be decoded back to original URL


    //Failure tests

    @Test
    public void create_fail() throws Exception {
        String request = requestToJson("");

        try {
            mockMvc.perform(
                    post("/tinyurl/create")
                            .header("Content-Type", "application/json")
                            .content(request))
                    .andExpect(status().is(200))
                    .andExpect(content().string(containsString("tinyurl/get/")));
        } catch (Exception e) {
            assertTrue(e.getCause() instanceof IllegalArgumentException);
            assertEquals("Request cannot be blank", e.getCause().getMessage());
        }
    }

    @Test
    public void getUnknown_fail() throws Exception {

        try {
            ResultActions getResult = mockMvc.perform(
                    get("/tinyurl/123"));
        } catch (Exception e){
            assertTrue(e.getCause() instanceof IllegalArgumentException);
            assertEquals("URL Not found", e.getCause().getMessage());
        }

    }

    private String requestToJson(String originalUrl) throws IOException {
        CreateRequest createRequest = new CreateRequest(originalUrl);
        ObjectMapper objectMapper = new ObjectMapper();
        StringWriter sw = new StringWriter();
        objectMapper.writeValue(sw, createRequest);
        return sw.toString();
    }
}
