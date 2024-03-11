package fi.iki.asb.xcc.examples.words;

import fi.iki.asb.xcc.examples.words.option.Direction;
import fi.iki.asb.xcc.examples.words.option.WordPlacement;

import java.util.List;
import java.util.function.Consumer;

public class WordsSolutionConsumer implements Consumer<List<WordPlacement>> {

    private final int width;

    private final char[] chars;

    private boolean solutionFound = false;

    public WordsSolutionConsumer(int width, int height) {
        this.width = width;
        this.chars = new char[width * height];
    }

    @Override
    public void accept(List<WordPlacement> solution) {
        for (WordPlacement wp: solution) {
            if (wp.dir() == Direction.HORIZONTAL) {
               horizontal(wp.row(), wp.word());
            } else {
                vertical(wp.col(), wp.word());
            }
        }

        solutionFound = true;
    }

    private void horizontal(int row, String word) {
        for (int i = 0; i < word.length(); i++) {
            chars[row * width + i] = word.charAt(i);
        }
    }

    private void vertical(int col, String word) {
        for (int i = 0; i < word.length(); i++) {
            chars[i * width + col] = word.charAt(i);
        }
    }

    public char[] getSolution() {
        return chars;
    }

    public boolean isSolutionFound() {
        return solutionFound;
    }
}
