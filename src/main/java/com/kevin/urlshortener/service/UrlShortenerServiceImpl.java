package com.kevin.urlshortener.service;

import com.kevin.urlshortener.entity.UrlEntry;
import com.kevin.urlshortener.repository.UrlShortenerRepository;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Random;

@Service
public class UrlShortenerServiceImpl implements UrlShortenerService {

    @Autowired
    UrlShortenerRepository urlShortenerRepository;

    @Override
    public String createShortUrl(String originalUrl) {
        //TODO Check if input URL is correct format e.g http/s / www. xyz . com
        if (!StringUtils.isNotBlank(originalUrl)){
            throw new IllegalArgumentException("Request cannot be blank");
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
        return result.getShortUrl();
    }

    @Override
    public String getOriginalUrl(String shortUrl) {
        UrlEntry originalUrl = urlShortenerRepository.findByShortUrl(shortUrl);
        if (originalUrl == null ){
            throw new IllegalArgumentException("URL Not found");
        }
        return originalUrl.getOriginalUrl();
    }
}
