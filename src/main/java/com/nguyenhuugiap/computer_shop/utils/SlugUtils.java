package com.nguyenhuugiap.computer_shop.utils;

import java.text.Normalizer;
import java.util.regex.Pattern;

public final class SlugUtils {
    public static String toSlug(String input) {
        if (input == null || input.isEmpty()) {
            return null;
        }
        String str = input.toLowerCase();
        str = str.replaceAll("đ", "d");
        String normalizer = Normalizer.normalize(str, Normalizer.Form.NFD);
        Pattern pattern = Pattern.compile("\\p{InCombiningDiacriticalMarks}+");
        str = pattern.matcher(normalizer).replaceAll("");
        str = str.replaceAll("[^a-z0-9\\s-]", "")
                .replaceAll("\\s+", "-");
        return str;
    }
}
