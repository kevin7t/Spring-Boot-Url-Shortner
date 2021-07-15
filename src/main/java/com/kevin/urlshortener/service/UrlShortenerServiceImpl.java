package com.kevin.urlshortener.service;

import com.kevin.urlshortener.entity.UrlEntry;
import com.kevin.urlshortener.repository.UrlShortenerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.Random;

@Service
public class UrlShortenerServiceImpl implements UrlShortenerService {

    @Autowired
    UrlShortenerRepository urlShortenerRepository;

    @Override
    public String createShortUrl(String originalUrl) {
        if (originalUrl == null) {
            //TODO Handle null original URL
        }

        //TODO cache before checking the DB
        //TODO if already exists then return
        UrlEntry alreadyExist = urlShortenerRepository.findByOriginalUrl(originalUrl);
        if (alreadyExist != null){
            return alreadyExist.getShortUrl();
        }

        UrlEntry urlEntry = new UrlEntry();
        urlEntry
                .setOriginalUrl(originalUrl)
                .setShortUrl(String.valueOf(new Random().nextInt()));
        UrlEntry result = urlShortenerRepository.save(urlEntry);
        //TODO Handle exceptions if anything prevents saving
        return result.getShortUrl();
    }

    @Override
    public String getOriginalUrl(String shortUrl) {
        UrlEntry originalUrl = urlShortenerRepository.findByShortUrl(shortUrl);
        if (originalUrl == null ){
            //TODO Exception handling
        }
        return originalUrl.getOriginalUrl();
    }
}
