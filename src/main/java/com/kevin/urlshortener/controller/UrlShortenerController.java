package com.kevin.urlshortener.controller;

import com.kevin.urlshortener.dto.CreateRequest;
import com.kevin.urlshortener.service.UrlShortenerService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.BufferedImageHttpMessageConverter;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.web.bind.annotation.*;
import net.glxn.qrgen.javase.QRCode;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.net.InetAddress;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.UnknownHostException;

import static org.springframework.http.HttpStatus.MOVED_PERMANENTLY;

@RestController
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
        URI uri = new URI(result);

        //Setting Location header will redirect the browser when the user clicks the URL in response
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setLocation(uri);
        return new ResponseEntity<>(httpHeaders, MOVED_PERMANENTLY);
    }

    @PostMapping(value = "/qr", produces = MediaType.IMAGE_PNG_VALUE)
    public ResponseEntity<BufferedImage> getOriginalUrlQr(@RequestBody CreateRequest createRequest) throws Exception {
        if (!StringUtils.isNotBlank(createRequest.getOriginalUrl())){
            throw new IllegalArgumentException("Request URL cannot be blank");
        }
        return new ResponseEntity<>(generateQRCodeImage(createRequest.getOriginalUrl()), HttpStatus.OK);
    }


    public static BufferedImage generateQRCodeImage(String barcodeText) throws Exception {
        ByteArrayOutputStream stream = QRCode
                .from(barcodeText)
                .withSize(250, 250)
                .stream();
        ByteArrayInputStream bis = new ByteArrayInputStream(stream.toByteArray());

        return ImageIO.read(bis);
    }

}
