package com.kevin.urlshortener.service;

import com.kevin.urlshortener.AbstractTest;
import com.kevin.urlshortener.entity.UrlEntry;
import com.kevin.urlshortener.repository.UrlShortenerRepository;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.test.context.junit4.SpringRunner;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@RunWith(SpringRunner.class)
public class UrlShortenerServiceImplTest extends AbstractTest {

    @MockBean
    UrlShortenerRepository urlShortenerRepository;

    @Autowired
    UrlShortenerService urlShortenerService;

    @TestConfiguration
    static class UrlShortenerServiceTestContextConfiguration {

        @Bean
        public UrlShortenerService urlShortenerService() {
            return new UrlShortenerServiceImpl();
        }
    }

    @Test
    public void testCreateWithNoEntry() {
        when(urlShortenerRepository.findByOriginalUrl("http://www.google.com")).thenReturn(null);
        UrlEntry urlEntry = new UrlEntry();
        String shortUrl = RandomStringUtils.random(20, true, true);
        urlEntry
                .setOriginalUrl("http://www.google.com")
                .setShortUrl(shortUrl);
        when(urlShortenerRepository.save(any())).thenReturn(urlEntry);

        String result = urlShortenerService.createShortUrl("http://www.google.com");
        assertEquals(shortUrl, result);
    }

    @Test
    public void testCreateWithExistingEntry() {
        UrlEntry urlEntry = new UrlEntry();
        String shortUrl = RandomStringUtils.random(20, true, true);
        urlEntry
                .setOriginalUrl("http://www.google.com")
                .setShortUrl(shortUrl);
        when(urlShortenerRepository.findByOriginalUrl("http://www.google.com")).thenReturn(urlEntry);

        //Verify that we do not save as it should already exist
        verify(urlShortenerRepository, never()).save(any());

        String result = urlShortenerService.createShortUrl("http://www.google.com");
        assertEquals(shortUrl, result);
    }

    @Test
    public void testGetOriginalUrl() {
        UrlEntry urlEntry = new UrlEntry();
        String shortUrl = RandomStringUtils.random(20, true, true);
        urlEntry
                .setOriginalUrl("http://www.google.com")
                .setShortUrl(shortUrl);
        when(urlShortenerRepository.findByShortUrl("xyz")).thenReturn(urlEntry);


        String result = urlShortenerService.getOriginalUrl("xyz");
        assertEquals(urlEntry.getOriginalUrl(), result);
    }

    @Test
    public void testGetOriginalUrlIsNull() {
        when(urlShortenerRepository.findByShortUrl("xyz")).thenReturn(null);

        try {
            urlShortenerService.getOriginalUrl("xyz");
            fail();
        } catch (Exception e) {
            assertTrue(e instanceof IllegalArgumentException);
            assertEquals("URL Not found", e.getMessage());
        }
    }

    @Test
    public void testCreateShortUrlBlankRequest() {
        try {
            urlShortenerService.createShortUrl(null);
            fail();
        } catch (Exception e) {
            assertTrue(e instanceof IllegalArgumentException);
            assertEquals("Request cannot be blank", e.getMessage());
        }
    }

    @Test
    public void testCreateShortUrlNotValid() {
        try {
            urlShortenerService.createShortUrl("xyzxyz");
            fail();
        } catch (Exception e) {
            assertTrue(e instanceof IllegalArgumentException);
            assertEquals("URL Is not valid", e.getMessage());
        }
    }

}