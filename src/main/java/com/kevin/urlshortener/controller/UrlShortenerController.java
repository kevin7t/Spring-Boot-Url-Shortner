package com.kevin.urlshortener.controller;

import com.kevin.urlshortener.service.UrlShortenerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.InetAddress;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.UnknownHostException;

import static org.springframework.http.HttpStatus.MOVED_PERMANENTLY;

@RestController
//TODO Change this to application property value
@RequestMapping("/tinyurl")

public class UrlShortenerController {
    @Autowired
    UrlShortenerService urlShortenerService;

    @Value("${server.port}")
    private int port;

    @GetMapping("/create/{originalUrl}")
    public ResponseEntity<?> createShortUrl(@PathVariable String originalUrl) throws UnknownHostException {
        //TODO change to POST + request object
        String shortUrl = urlShortenerService.createShortUrl(originalUrl);
        String ip = InetAddress.getLocalHost().getHostAddress();

        //TODO StringBuilder instead of concat
        return ResponseEntity.ok("http://" + ip + ":" + port + "/tinyurl/get/" + shortUrl);
    }


    @GetMapping("/get/{shortUrl}")
    public ResponseEntity<?> getOriginalUrl(@PathVariable String shortUrl) throws URISyntaxException {
        String result = urlShortenerService.getOriginalUrl(shortUrl);
        //TODO Check if result begins with http:// if not add it on
        result = "http://" + result;
        URI uri = new URI(result);

        //Setting Location header will redirect the browser
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setLocation(uri);
        return new ResponseEntity<>(httpHeaders, MOVED_PERMANENTLY);
    }


}
