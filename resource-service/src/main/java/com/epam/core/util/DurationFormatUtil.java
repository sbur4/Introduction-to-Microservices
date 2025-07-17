package com.epam.core.util;

import com.epam.core.exception.DurationFormatException;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.apache.commons.numbers.core.Precision;

@Slf4j
@UtilityClass
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class DurationFormatUtil {

    static int DIVISION = 100;
    static int SCALE = 2;
    static String SEPARATOR = ".";
    static String FORMAT = "%02d:%02d";

    public static String formatDuration(final String rawDuration) {
        log.debug("Attempting to format duration: '{}'", rawDuration);

        validateInputData(rawDuration);

        double totalDuration = NumberUtils.toDouble(rawDuration) / DIVISION;
        double roundedDuration = Precision.round(totalDuration, SCALE);
        String splittedSeconds = StringUtils.substringAfter(Double.toString(roundedDuration), SEPARATOR);

        int minutes = (int) totalDuration;
        int seconds = Integer.parseInt(splittedSeconds);

        String formattedDuration = String.format(FORMAT, minutes, seconds);
        log.debug("Formatted duration: '{}'", formattedDuration);

        return formattedDuration;
    }

    private void validateInputData(String rawDuration) {
        if (StringUtils.isBlank(rawDuration)) {
            log.error("Input duration string is null or empty.");
            throw new DurationFormatException("Input string is null or empty");
        }

        if (!NumberUtils.isParsable(rawDuration)) {
            log.error("Input duration string '{}' is not a valid number.", rawDuration);
            throw new DurationFormatException("Input duration string is not a valid number: " + rawDuration);
        }
    }
}
