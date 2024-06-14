package com.plenigo.project.service;

import java.nio.file.Path;
import java.time.LocalDate;

import com.plenigo.project.domain.Image;

public interface ImageService {

    Image[] fetchImagesForDate(LocalDate date);

    Image[] fetchAllImages();

    void saveImage(Image image, Path targetFolder);

}
