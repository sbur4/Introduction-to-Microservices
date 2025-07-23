package com.epam.core.util;

import lombok.experimental.UtilityClass;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.Base64;

@UtilityClass
public class ChecksumUtil {

    public static String generateChecksum(byte[] rawSong) {
        return rawSong.length == 0 ? StringUtils.EMPTY : getCheckSum(rawSong);
    }

    private String getCheckSum(byte[] rawSong) {
        byte[] hashBytes = DigestUtils.sha256(rawSong);
        return Base64.getEncoder().encodeToString(hashBytes);
    }
}
