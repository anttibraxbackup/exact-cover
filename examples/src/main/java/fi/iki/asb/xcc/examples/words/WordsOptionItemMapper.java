package fi.iki.asb.xcc.examples.words;

import fi.iki.asb.xcc.OptionItemMapper;
import fi.iki.asb.xcc.examples.words.item.CellOccupation;
import fi.iki.asb.xcc.examples.words.item.ColOccupation;
import fi.iki.asb.xcc.examples.words.item.RowOccupation;
import fi.iki.asb.xcc.examples.words.item.WordConsumption;
import fi.iki.asb.xcc.examples.words.option.Direction;
import fi.iki.asb.xcc.examples.words.option.WordPlacement;

import java.util.ArrayList;
import java.util.Collection;

public class WordsOptionItemMapper implements OptionItemMapper<WordPlacement> {

    @Override
    public Collection<Object> from(WordPlacement option) {
        final Collection<Object> items = new ArrayList<>();
        items.add(new WordConsumption(option.word()));
        if (option.dir() == Direction.HORIZONTAL) {
            horizontal(option.row(), option.word(), items);
        } else {
            vertical(option.col(), option.word(), items);
        }
        return items;
    }

    private void horizontal(int row, String word, Collection<Object> items) {
        items.add(new RowOccupation(row));
        for (int i = 0; i < word.length(); i++) {
            items.add(new CellOccupation(row, i, word.charAt(i)));
        }
    }

    private void vertical(int col, String word, Collection<Object> items) {
        items.add(new ColOccupation(col));
        for (int i = 0; i < word.length(); i++) {
            items.add(new CellOccupation(i, col, word.charAt(i)));
        }
    }
}
