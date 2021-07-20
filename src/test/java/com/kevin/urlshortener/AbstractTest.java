package com.kevin.urlshortener;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kevin.urlshortener.controller.UrlShortenerController;
import com.kevin.urlshortener.dto.CreateRequest;
import com.kevin.urlshortener.service.UrlShortenerService;
import org.apache.commons.io.IOUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;

import static org.hamcrest.Matchers.containsString;
import static org.junit.Assert.assertEquals;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class AbstractTest {

    public static byte[] toByteArray(BufferedImage bi, String format)
            throws IOException {

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write(bi, format, baos);
        byte[] bytes = baos.toByteArray();
        return bytes;

    }

    public String requestToJson(String originalUrl) throws IOException {
        CreateRequest createRequest = new CreateRequest(originalUrl);
        ObjectMapper objectMapper = new ObjectMapper();
        StringWriter sw = new StringWriter();
        objectMapper.writeValue(sw, createRequest);
        return sw.toString();
    }

    public String loadFile(String filepath) throws IOException {
        InputStream inputStream = this.getClass().getResourceAsStream(filepath);
        String data = IOUtils.toString(inputStream);
        return data;
    }


}