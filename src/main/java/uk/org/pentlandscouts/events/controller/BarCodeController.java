package uk.org.pentlandscouts.events.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.BufferedImageHttpMessageConverter;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.web.bind.annotation.*;
import uk.org.pentlandscouts.events.qrcode.QRCodeGenerator;


import java.awt.image.BufferedImage;

@CrossOrigin("*")
@RestController
@RequestMapping("/barcodes")

public class BarCodeController {

    private static final Logger logger = LoggerFactory.getLogger(BarCodeController.class);

    @GetMapping(value = "/qrcode/", produces = MediaType.IMAGE_PNG_VALUE)
    public ResponseEntity<BufferedImage> qrBarcodeURL( @RequestParam(value="url") String url)
            throws Exception {

        logger.info("Serving the following QRCode: {}",url);
        return okResponse(QRCodeGenerator.generateQRCodeImage(url));
    }

    private ResponseEntity<BufferedImage> okResponse(BufferedImage image) {
        return new ResponseEntity<>(image, HttpStatus.OK);
    }
    @Bean
    public HttpMessageConverter<BufferedImage> createImageHttpMessageConverter() {
        return new BufferedImageHttpMessageConverter();
    }
}
