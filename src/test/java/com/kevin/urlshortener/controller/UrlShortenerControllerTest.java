package com.kevin.urlshortener.controller;

import com.kevin.urlshortener.AbstractTest;
import com.kevin.urlshortener.service.UrlShortenerService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;

import static org.hamcrest.Matchers.containsString;
import static org.junit.Assert.assertEquals;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@WebMvcTest(UrlShortenerController.class)
public class UrlShortenerControllerTest extends AbstractTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    UrlShortenerService urlShortenerService;

    @Test
    public void testCreateShortUrl() throws Exception {
        String request = requestToJson("http://www.google.com");

        when(urlShortenerService.createShortUrl("http://www.google.com")).thenReturn("xyz");

        mockMvc.perform(
                post("/tinyurl/create")
                        .header("Content-Type", "application/json")
                        .content(request))
                .andExpect(status().is(200))
                .andExpect(content().string(containsString("http://127.0.0.1:8080/tinyurl/xyz")));

    }

    @Test
    public void testGetOriginalUrl() throws Exception {

        when(urlShortenerService.getOriginalUrl("xyz")).thenReturn("http://www.google.com");

        ResultActions getResult = mockMvc.perform(
                get("http://127.0.0.1:8080/tinyurl/xyz"))
                .andExpect(status().is(301));

        //Assert response code is 301 - Moved permanently
        //Assert Location header is the redirected webpage therefore causing browser redirect
        assertEquals("", getResult.andReturn().getResponse().getContentAsString());
        //Asserting with Http:// as the code will add it, if not exist
        assertEquals("http://www.google.com", getResult.andReturn().getResponse().getHeader("Location"));
    }

    @Test
    public void testGetOriginalUrlQR() throws Exception {
        BufferedImage qrCodeImage = UrlShortenerController.generateQRCodeImage("http://www.google.com");

        String request = requestToJson("http://www.google.com");

        ResultActions result = mockMvc.perform(
                post("http://127.0.0.1:8080/tinyurl/qr")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.IMAGE_PNG_VALUE)
                        .content(request)).andExpect(status().is(200));


        BufferedImage image = ImageIO.read(new ByteArrayInputStream(result.andReturn().getResponse().getContentAsByteArray()));

        //Asserting the QR code is the same generated using http://www.google.com
        assertArrayEquals(toByteArray(qrCodeImage, "png"), toByteArray(image, "png"));
    }

    @Test
    public void testCreateShortUrl_Failure() throws Exception {
        String request = requestToJson("hp://www.google.com");

        when(urlShortenerService.createShortUrl("hp://www.google.com")).thenThrow(new IllegalArgumentException("URL Is not valid"));

        ResultActions getResult = mockMvc.perform(
                post("/tinyurl/create")
                        .header("Content-Type", "application/json")
                        .content(request))
                .andExpect(status().is(500))
                .andExpect(content().string("{\"statusMessage\":\"500 INTERNAL_SERVER_ERROR\",\"errorMessage\":\"URL Is not valid\"}"));
    }

    @Test
    public void testGetOriginalUrl_Failure() throws Exception {
        when(urlShortenerService.getOriginalUrl("xyz")).thenThrow(new IllegalArgumentException("URL Not found"));

        ResultActions getResult = mockMvc.perform(
                get("http://127.0.0.1:8080/tinyurl/xyz"))
                .andExpect(status().is(500))
                .andExpect(content().string("{\"statusMessage\":\"500 INTERNAL_SERVER_ERROR\",\"errorMessage\":\"URL Not found\"}"));
    }
}