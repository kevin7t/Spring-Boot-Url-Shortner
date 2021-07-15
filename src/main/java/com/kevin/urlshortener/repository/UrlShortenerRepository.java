package com.kevin.urlshortener.repository;

import com.kevin.urlshortener.entity.UrlEntry;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UrlShortenerRepository extends JpaRepository<UrlEntry, Long> {
    public UrlEntry findByShortUrl(String shortUrl);
    public UrlEntry findByOriginalUrl(String originalUrl);
}
