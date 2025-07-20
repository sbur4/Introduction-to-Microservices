package com.epam.core.util;

import com.epam.core.exception.AudioParsingException;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import org.apache.tika.exception.TikaException;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.parser.ParseContext;
import org.apache.tika.parser.mp3.Mp3Parser;
import org.apache.tika.sax.BodyContentHandler;
import org.xml.sax.SAXException;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Map;

@Slf4j
@UtilityClass
public class AudioParserUtil {

    public static Metadata parseMp3File(final byte[] audioFile) {
        log.debug("Starting MP3 file parsing.");

        BodyContentHandler handler = new BodyContentHandler();
        Metadata metadata = new Metadata();
        ParseContext parseContext = new ParseContext();
        Mp3Parser mp3Parser = new Mp3Parser();

        try (ByteArrayInputStream inputStream = new ByteArrayInputStream(audioFile)) {
            mp3Parser.parse(inputStream, handler, metadata, parseContext);
            log.debug("MP3 file parsing completed successfully.");
        } catch (IOException | SAXException | TikaException ex) {
            log.error("TikaException occurred while parsing the MP3 file.", ex);
            Map<String, String> errorDetails = Map.of("Mp3Parser by Tika", ex.getMessage());
            throw new AudioParsingException("TikaException occurred while parsing the MP3 file.", errorDetails);
        }

        return metadata;
    }
}
