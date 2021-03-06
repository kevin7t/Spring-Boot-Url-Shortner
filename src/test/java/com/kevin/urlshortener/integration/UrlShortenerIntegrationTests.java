package com.kevin.urlshortener.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kevin.urlshortener.AbstractTest;
import com.kevin.urlshortener.UrlshortenerApplication;
import com.kevin.urlshortener.controller.UrlShortenerController;
import com.kevin.urlshortener.dto.CreateRequest;
import org.apache.commons.io.IOUtils;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = UrlshortenerApplication.class)
@AutoConfigureMockMvc
class UrlShortenerIntegrationTests extends AbstractTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    public void create_success() throws Exception {
        String request = requestToJson("http://www.google.com");

        mockMvc.perform(
                post("/tinyurl/create")
                        .header("Content-Type", "application/json")
                        .content(request))
                .andExpect(status().is(200))
                .andExpect(content().string(containsString("http://127.0.0.1:8080/tinyurl/")));

    }

    @Test
    public void getAfterCreate_success() throws Exception {
        final String REQUEST_URL = "http://www.google.com";

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
        assertEquals(REQUEST_URL, getResult.andReturn().getResponse().getHeader("Location"));
    }


    //Failure tests

    @Test
    public void create_fail() throws Exception {
        String request = requestToJson("");

        try {
            mockMvc.perform(
                    post("/tinyurl/create")
                            .header("Content-Type", "application/json")
                            .content(request))
                    .andExpect(status().is(500))
                    .andExpect(content().string(equalTo(loadFile("/samples/InternalServerError.json"))));
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
}
