package com.plenigo.project.service;

import static com.plenigo.project.config.WebConstant.API_KEY;
import static com.plenigo.project.config.WebConstant.DATE_FORMAT;
import static com.plenigo.project.config.WebConstant.DATE_TIME_FORMAT;
import static com.plenigo.project.config.WebConstant.EPIC_BASE_URL;
import static com.plenigo.project.config.WebConstant.EPIC_FETCH_ALL;
import static com.plenigo.project.config.WebConstant.EPIC_FETCH_IMAGES_BY_DATE_URL;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import com.plenigo.project.domain.Image;
import org.apache.commons.io.FileUtils;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class ImageServiceImpl implements ImageService {

    private final RestTemplate restTemplate = new RestTemplate();

    @Override
    public Image[] fetchImagesForDate(LocalDate date) {

        try {
            String formattedDate = date.format(DateTimeFormatter.ofPattern(DATE_FORMAT));
            String apiUrl = EPIC_FETCH_IMAGES_BY_DATE_URL + formattedDate + "?api_key=" + API_KEY;
            return restTemplate.getForObject(new URI(apiUrl), Image[].class);
        } catch (Exception e) {
            throw new RuntimeException("Error fetching images for date " + date + ": " + e.getMessage(), e);
        }
    }

    @Override
    public Image[] fetchAllImages() {

        try {
            String apiUrl = EPIC_FETCH_ALL + "?api_key=" + API_KEY;
            return restTemplate.getForObject(apiUrl, Image[].class);
        } catch (Exception e) {
            throw new RuntimeException("Error fetching all images: " + e.getMessage(), e);
        }
    }

    private String buildImageUrl(Image image, int year, String month, String day) {

        return EPIC_BASE_URL + year + "/" + month + "/" + day + "/jpg/" + image.getImage() + ".jpg";
    }

    @Override
    public void saveImage(Image image, Path targetFolder) {

        try {
            LocalDateTime dateTime = LocalDateTime.parse(image.getDate(), DateTimeFormatter.ofPattern(DATE_TIME_FORMAT));
            int year = dateTime.getYear();
            String month = String.format("%02d", dateTime.getMonthValue());
            String day = String.format("%02d", dateTime.getDayOfMonth());

            String imageUrl = buildImageUrl(image, year, month, day);
            byte[] imageData = restTemplate.getForObject(imageUrl, byte[].class);

            if (imageData != null && imageData.length > 0) {
                File imageFile = targetFolder.resolve(image.getImage() + ".jpg").toFile();
                FileUtils.writeByteArrayToFile(imageFile, imageData);
            } else {
                System.err.println("Empty or null image data received for image: " + image.getImage());
            }
        } catch (IOException e) {
            throw new RuntimeException("Error saving image " + image.getImage() + ": " + e.getMessage(), e);
        }
    }
}
