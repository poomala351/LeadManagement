package com.example.LeadManagement.util;

import com.example.LeadManagement.exception.InvalidFileException;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Component
public class FileValidationUtil {

    private static final long MAX_IMAGE_SIZE = 10 * 1024 * 1024;
    private static final long MAX_VIDEO_SIZE = 50 * 1024 * 1024;

    private static final List<String> IMAGE_TYPES = List.of(
            "image/jpeg",
            "image/jpg",
            "image/png"
    );

    private static final List<String> VIDEO_TYPES = List.of(
            "video/mp4"
    );

    private static final List<String> IMAGE_EXTENSIONS = List.of(
            ".jpg",
            ".jpeg",
            ".png"
    );

    private static final List<String> VIDEO_EXTENSIONS = List.of(
            ".mp4"
    );

    public void validate(MultipartFile file) {

        if (file == null || file.isEmpty()) {
            throw new InvalidFileException("File cannot be empty");
        }

        String contentType = file.getContentType();

        if (contentType == null) {
            throw new InvalidFileException("Unable to determine file type");
        }

        String extension = getExtension(file);

        // Image
        if (IMAGE_TYPES.contains(contentType)) {

            if (!IMAGE_EXTENSIONS.contains(extension)) {
                throw new InvalidFileException(
                        "Only .jpg, .jpeg and .png images are allowed");
            }

            if (file.getSize() > MAX_IMAGE_SIZE) {
                throw new InvalidFileException(
                        "Image size should not exceed 10 MB");
            }

            return;
        }

        // Video
        if (VIDEO_TYPES.contains(contentType)) {

            if (!VIDEO_EXTENSIONS.contains(extension)) {
                throw new InvalidFileException(
                        "Only .mp4 videos are allowed");
            }

            if (file.getSize() > MAX_VIDEO_SIZE) {
                throw new InvalidFileException(
                        "Video size should not exceed 50 MB");
            }

            return;
        }

        throw new InvalidFileException(
                "Only JPG, JPEG, PNG images and MP4 videos are supported");
    }

    private String getExtension(MultipartFile file) {

        String fileName = file.getOriginalFilename();

        if (fileName == null || !fileName.contains(".")) {
            throw new InvalidFileException("Invalid file name");
        }

        return fileName.substring(fileName.lastIndexOf(".")).toLowerCase();
    }
}