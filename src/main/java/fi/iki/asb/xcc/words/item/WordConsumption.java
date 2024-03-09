package fi.iki.asb.xcc.words.item;

import fi.iki.asb.xcc.SecondaryItem;

/**
 * Secondary item representing a word being consumed. This ensures that
 * each word is used only once.
 */
public record WordConsumption(String word) implements SecondaryItem {
}
