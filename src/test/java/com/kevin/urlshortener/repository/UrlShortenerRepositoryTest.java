package com.kevin.urlshortener.repository;

import com.kevin.urlshortener.AbstractTest;
import com.kevin.urlshortener.entity.UrlEntry;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.SpringRunner;

import static org.junit.jupiter.api.Assertions.*;

@RunWith(SpringRunner.class)
@DataJpaTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class UrlShortenerRepositoryTest extends AbstractTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    UrlShortenerRepository urlShortenerRepository;


    @Test
    public void testFindByShortUrl(){
        UrlEntry urlEntry = new UrlEntry();
        urlEntry.setShortUrl("shortUrl").setOriginalUrl("originalUrl");

        entityManager.persist(urlEntry);
        entityManager.flush();

        UrlEntry result = urlShortenerRepository.findByShortUrl("shortUrl");
        assertEquals(1L, result.getId());
        assertEquals("shortUrl", result.getShortUrl());
        assertEquals("originalUrl", result.getOriginalUrl());
    }
    @Test
    public void testFindByOriginalUrl(){
        UrlEntry urlEntry = new UrlEntry();
        urlEntry.setShortUrl("shortUrl").setOriginalUrl("originalUrl");

        entityManager.persist(urlEntry);
        entityManager.flush();

        UrlEntry result = urlShortenerRepository.findByOriginalUrl("originalUrl");
        assertEquals(1L, result.getId());
        assertEquals("shortUrl", result.getShortUrl());
        assertEquals("originalUrl", result.getOriginalUrl());
    }
}