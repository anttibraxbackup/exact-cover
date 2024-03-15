package fi.iki.asb.xcc.examples.words;

import fi.iki.asb.xcc.XCC;
import fi.iki.asb.xcc.LinkedXCC;
import fi.iki.asb.xcc.examples.words.option.WordPlacement;

/**
 * Fill two-dimensional matrix with words.
 */
public class WordsSolver {

    private final int width;
    private final int height;
    private final XCC<WordPlacement> xcc;

    private final WordsSolutionConsumer solutionConsumer;

    public WordsSolver(int width, int height) {
        this.width = width;
        this.height = height;
        this.xcc = new LinkedXCC<>(new WordsItemProvider());
        this.solutionConsumer = new WordsSolutionConsumer(width, height);
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
