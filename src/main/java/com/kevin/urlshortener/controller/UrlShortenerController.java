package com.kevin.urlshortener.controller;

import com.kevin.urlshortener.dto.CreateRequest;
import com.kevin.urlshortener.service.UrlShortenerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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

    private String ip;

    public UrlShortenerController() throws UnknownHostException {
        ip = InetAddress.getLocalHost().getHostAddress();
    }
    //TODO Add exception mapping to json output here when calling urlShortenerService

    @PostMapping("/create")
    public ResponseEntity<?> createShortUrl(@RequestBody CreateRequest createRequest) throws UnknownHostException {
        String shortUrl = urlShortenerService.createShortUrl(createRequest.getOriginalUrl());

        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("http://").append(ip).append(":").append(port).append("/tinyurl/").append(shortUrl);
        return ResponseEntity.ok(stringBuilder.toString());
    }


    @GetMapping("/{shortUrl}")
    public ResponseEntity<?> getOriginalUrl(@PathVariable String shortUrl) throws URISyntaxException {
        String result = urlShortenerService.getOriginalUrl(shortUrl);
        if (!result.startsWith("http://")) {
            result = "http://" + result;
        }
        URI uri = new URI(result);

        //Setting Location header will redirect the browser
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setLocation(uri);
        return new ResponseEntity<>(httpHeaders, MOVED_PERMANENTLY);
    }


}
