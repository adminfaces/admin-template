package com.github.adminfaces.template.util;

import java.io.Serializable;
import java.util.Collection;
import java.util.Map;

/**
 * Created by rafael-pestano on 26/06/2015. assertions utils
 *
 */
public class Assert implements Serializable {


    private static final long serialVersionUID = 1L;

    private Assert() {

    }

    /**
     * @param object object to check if null
     * @return TRUE assertion when given objects is not null, FALSE otherwise
     */
    public static boolean has(Object object) {
        return object != null;
    }

    /**
     * @param text text to check for characters
     * @return TRUE when given text has any character, FALSE otherwise
     */
    public static boolean has(String text) {
        if (text != null && text.trim().length() > 0) {
            return true;
        }
        return false;
    }

    /**
     * @param textToSearch the text to search
     * @param substring string to check in text
     * @return TRUE when given text contains the given substring, FALSE otherwise
     */
    public static boolean contains(String textToSearch, String substring) {
        if (textToSearch != null && textToSearch.contains(substring)) {
            return true;
        }
        return false;
    }

    /**
     * @param array array to check for presence of elements
     * @return TRUE when given array has elements; that is, it must not be {@code null} and must
     *         have at least one element. FALSE otherwise
     */
    public static boolean has(Object[] array) {
        if (array == null || array.length == 0) {
            return false;
        }
        for (Object element : array) {
            if (element != null) {
                return true;
            }
        }
        return false;
    }

    /**
     * @param collection collection to check for presence of elements
     * @return TRUE when given collection has elements; that is, it must not be {@code null} and
     *         must have at least one element. @return FALSE otherwise
     */
    public static boolean has(Collection<?> collection) {
        if (collection != null && !collection.isEmpty()) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * @param map map to check entries
     * @return TRUE if given Map has entries; that is, it must not be {@code null} and must have at
     *         least one entry. Queue FALSE otherwise
     */
    public static boolean has(Map<?, ?> map) {
        if (map == null) {
            return false;
        }
        if (has(map.entrySet().toArray())) {
            return true;
        }
        return false;
    }
    
    public static boolean startsWithOneOf(String string, String... prefixes) {
		for (String prefix : prefixes) {
			if (string.startsWith(prefix)) {
				return true;
			}
		}
		return false;
	}

}