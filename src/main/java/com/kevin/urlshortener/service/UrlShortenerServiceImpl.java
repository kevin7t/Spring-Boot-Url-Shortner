package com.kevin.urlshortener.service;

import com.kevin.urlshortener.entity.UrlEntry;
import com.kevin.urlshortener.repository.UrlShortenerRepository;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.validator.routines.UrlValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UrlShortenerServiceImpl implements UrlShortenerService {

    @Autowired
    private UrlShortenerRepository urlShortenerRepository;

    private UrlValidator urlValidator;

    public UrlShortenerServiceImpl() {
        this.urlValidator = new UrlValidator();
    }


    @Override
    public String createShortUrl(String originalUrl) {
        if (!StringUtils.isNotBlank(originalUrl)){
            throw new IllegalArgumentException("Request cannot be blank");
        }

        if (!urlValidator.isValid(originalUrl)){
            throw new IllegalArgumentException("URL Is not valid");
        }

        UrlEntry alreadyExist = urlShortenerRepository.findByOriginalUrl(originalUrl);
        if (alreadyExist != null){
            return alreadyExist.getShortUrl();
        }

        UrlEntry urlEntry = new UrlEntry();
        urlEntry
                .setOriginalUrl(originalUrl)
                .setShortUrl(RandomStringUtils.random(20, true, true));
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
