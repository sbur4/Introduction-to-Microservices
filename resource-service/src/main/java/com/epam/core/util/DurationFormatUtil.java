package com.epam.core.util;

import com.epam.core.exception.DurationFormatException;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.ParseException;
import java.util.Locale;

@Slf4j
@UtilityClass
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class DurationFormatUtil {

    static int MIM_TO_SEC = 60;
    static String DOUBLE_FORMAT = "0.0";
    static String TIME_FORMAT = "%02d:%02d";
    static String DEFAULT_VALUE = "00:00";

    public static String formatDuration(final String rawDuration) {
        log.debug("Attempting to format duration: '{}'", rawDuration);

        try {
            validateInputData(rawDuration);

            DecimalFormat df = new DecimalFormat(DOUBLE_FORMAT, DecimalFormatSymbols.getInstance(Locale.ENGLISH));
            double duration = df.parse(rawDuration).doubleValue();

            int totalSeconds = (int) Math.abs(duration);

            int minutes = totalSeconds / MIM_TO_SEC;
            int seconds = totalSeconds % MIM_TO_SEC;

            String formattedDuration = String.format(TIME_FORMAT, minutes, seconds);
            log.debug("Formatted duration: '{}'", formattedDuration);
            return formattedDuration;
        } catch (ParseException e) {
            log.error("Failed to parse duration value: '{}'. Error: {}", rawDuration, e.getMessage());
            log.debug("Stack trace:", e);
        } catch (DurationFormatException e) {
            log.error("Validation error for duration: '{}'. Error: {}", rawDuration, e.getMessage());
        } catch (Exception e) {
            log.error("Unexpected error formatting duration: '{}'. Error: {}", rawDuration, e.getMessage());
            log.debug("Stack trace:", e);
        }
        log.warn("Returning default duration value for input: '{}'", rawDuration);
        return DEFAULT_VALUE;
    }

    private void validateInputData(String rawDuration) {
        if (StringUtils.isBlank(rawDuration)) {
            log.error("Input duration string is null or empty.");
            throw new DurationFormatException("Input string is null or empty.");
        }

        if (!NumberUtils.isParsable(rawDuration)) {
            log.error("Input duration string '{}' is not a valid number.", rawDuration);
            throw new DurationFormatException("Input duration string is not a valid number: " + rawDuration);
        }
    }
}
