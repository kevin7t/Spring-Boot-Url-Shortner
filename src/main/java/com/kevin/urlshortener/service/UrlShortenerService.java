package com.kevin.urlshortener.service;

public interface UrlShortenerService {
    public String createShortUrl(String originalUrl);
    public String getOriginalUrl(String shortUrl);
}
