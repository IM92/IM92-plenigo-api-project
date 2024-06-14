package com.plenigo.project.command;

import static com.plenigo.project.config.WebConstant.DATE_FORMAT;
import static com.plenigo.project.config.WebConstant.DATE_FORMAT_N2;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

import com.plenigo.project.domain.Image;
import com.plenigo.project.service.ImageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import org.springframework.shell.standard.ShellOption;

@ShellComponent
public class ImageShellCommand {

    private static final Logger logger = LoggerFactory.getLogger(ImageShellCommand.class);

    @Autowired
    private ImageService imageService;

    @ShellMethod(value = "Fetch and store NASA EPIC images for a given date", key = "fetch")
    public String fetchEpicImages(@ShellOption String targetFolder, @ShellOption(defaultValue = "") String date) {

        if (targetFolder == null || targetFolder.trim().isEmpty()) {
            logger.error("Target folder must be specified.");
            return "Error: Target folder must be specified.";
        }

        LocalDate parsedDate;
        if (date == null || date.trim().isEmpty()) {
            String lastAvailableDate = getLastAvailableDate();
            parsedDate = LocalDate.parse(lastAvailableDate, DateTimeFormatter.ofPattern(DATE_FORMAT));
        } else {
            try {
                parsedDate = LocalDate.parse(date, DateTimeFormatter.ofPattern(DATE_FORMAT));
                LocalDate today = LocalDate.now();

                if (parsedDate.isAfter(today)) {
                    throw new IllegalArgumentException("Date cannot be in the future: " + date);
                }
            } catch (DateTimeParseException e) {
                logger.error("Invalid date format! Please enter the following format: YYYY-MM-DD");
                return "Error: Invalid date format! Please enter the following format: YYYY-MM-DD";
            }
        }

        Image[] images = imageService.fetchImagesForDate(parsedDate);
        if (images == null || images.length == 0) {
            logger.warn("No images found for the specified date: {}", date);
            return "No images found for the specified date: " + date;
        }

        String formattedDate = parsedDate.format(DateTimeFormatter.ofPattern(DATE_FORMAT_N2));
        Path targetFolderDir = Paths.get(targetFolder, formattedDate);
        try {
            Files.createDirectories(targetFolderDir);
        } catch (IOException e) {
            logger.error("Error creating target directory: {}. {}", targetFolderDir, e.getMessage());
            return "Error creating target directory: " + targetFolderDir + ". " + e.getMessage();
        }

        try {
            for (Image image : images) {
                imageService.saveImage(image, targetFolderDir);
            }
            logger.info("Images fetched and saved successfully to {}", targetFolderDir.toAbsolutePath());
            return "Images fetched and saved successfully to " + targetFolderDir.toAbsolutePath();
        } catch (Exception e) {
            logger.error("Error fetching or saving images: {}", e.getMessage());
            return "Error fetching or saving images: " + e.getMessage();
        }
    }

    private String getLastAvailableDate() {
        return imageService.fetchAllImages()[0].getDate().substring(0, 10);
    }
}
