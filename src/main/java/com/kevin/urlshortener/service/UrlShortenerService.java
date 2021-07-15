package com.kevin.urlshortener.service;

import org.springframework.stereotype.Service;

public interface UrlShortenerService {
    public String createShortUrl(String originalUrl);
    public String getOriginalUrl(String shortUrl);
}
