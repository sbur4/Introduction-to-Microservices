package com.epam.core.dto.request;

import com.epam.core.validation.constraint.DurationValidationConstraint;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Value;

@Value
public class SongMetadataRequestDto {

    @Min(value = 1, message = "Numeric, must match an existing Resource ID.")
    int id;

    @NotBlank(message = "Name cannot be blank.")
    @Size(min = 1, max = 100, message = "Track name must contains 1-100 characters text.")
    String name;

    @NotBlank(message = "Artist cannot be blank.")
    @Size(min = 1, max = 100, message = "Artist name must contains 1-100 characters text.")
    String artist;

    @NotBlank(message = "Album cannot be blank.")
    @Size(min = 1, max = 100, message = "Album name must contains 1-100 characters text.")
    String album;

    @DurationValidationConstraint
//    @Pattern(regexp = "^\\d{2}:\\d{2}$", message = "Format mm:ss, with leading zeros.")
    String duration;

    @Pattern(regexp = "^(19|20)\\d{2}$", message = "YYYY format between 1900-2099.")
    @NotBlank(message = "Year cannot be blank.")
    String year;
}
