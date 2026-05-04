package com.cpmss.util;

import com.github.slugify.Slugify;

/**
 * Converts strings to URL-safe slugs.
 *
 * <p>Wraps the Slugify library. Handles Unicode, diacritics, spaces,
 * and special characters: "Some Name" → "some-name", "Résumé" → "resume".
 */
public final class SlugUtils {

    private static final Slugify SLUGIFY = Slugify.builder().build();

    private SlugUtils() {}

    /**
     * Generates a URL-safe slug from the given name.
     *
     * @param name source string (e.g. "Some Name")
     * @return lowercase, hyphen-separated slug (e.g. "some-name")
     */
    public static String generate(String name) {
        return SLUGIFY.slugify(name);
    }
}
