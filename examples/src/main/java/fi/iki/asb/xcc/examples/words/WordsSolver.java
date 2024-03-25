package fi.iki.asb.xcc.examples.words;

import fi.iki.asb.xcc.XCC;
import fi.iki.asb.xcc.LinkedXCC;
import fi.iki.asb.xcc.examples.queen.QueenGrid;
import fi.iki.asb.xcc.examples.queen.QueenItemProvider;
import fi.iki.asb.xcc.examples.queen.option.QueenPlacement;
import fi.iki.asb.xcc.examples.words.option.WordPlacement;

import java.util.function.Consumer;
import java.util.function.Function;

/**
 * Fill two-dimensional matrix with words.
 */
public final class WordsSolver {

    private final int width;

    private final int height;

    private final XCC<WordPlacement> xcc;

    private final WordsSolutionConsumer solutionConsumer;

    public WordsSolver(
            final int width,
            final int height,
            final Function<WordsItemProvider, XCC<WordPlacement>> xccInitializer) {
        this.width = width;
        this.height = height;
        this.xcc = xccInitializer.apply(new WordsItemProvider());
        this.solutionConsumer = new WordsSolutionConsumer(width, height);
    }

    public WordsSolver(int width, int height) {
        this(width, height, LinkedXCC::new);
    }

    public void addWord(String word) {
        if (word.length() == width) {
            for (int row = 0; row < height; row++) {
                xcc.addOption(WordPlacement.horizontal(row, word));
            }
        }

        if (word.length() == height) {
            for (int col = 0; col < width; col++) {
                xcc.addOption(WordPlacement.vertical(col, word));
            }
        }
    }

    public void solve() {
        xcc.search(solutionConsumer, solutionConsumer::isSolutionFound);
    }

    public char[] getSolution() {
        return solutionConsumer.getSolution();
    }
}
