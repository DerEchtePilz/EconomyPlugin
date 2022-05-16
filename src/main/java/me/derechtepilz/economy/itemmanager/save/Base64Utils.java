package me.derechtepilz.economy.itemmanager.save;

import java.util.Base64;

public class Base64Utils {
    public String encodeBase64(String toEncode) {
        return Base64.getEncoder().encodeToString(toEncode.getBytes());
    }

    public String decodeBase64(String toDecode) {
        return new String(Base64.getDecoder().decode(toDecode));
    }
}
