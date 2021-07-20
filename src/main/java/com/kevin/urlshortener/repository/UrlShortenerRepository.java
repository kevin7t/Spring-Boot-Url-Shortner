package com.kevin.urlshortener.repository;

import com.kevin.urlshortener.entity.UrlEntry;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UrlShortenerRepository extends JpaRepository<UrlEntry, Long> {
    @Cacheable("shortUrl")
    public UrlEntry findByShortUrl(String shortUrl);
    @Cacheable("originalUrl")
    public UrlEntry findByOriginalUrl(String originalUrl);
}
