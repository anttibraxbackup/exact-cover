package fi.iki.asb.xcc.words;

import fi.iki.asb.xcc.DLX;
import fi.iki.asb.xcc.LinkedDLX;
import fi.iki.asb.xcc.words.option.WordPlacement;

/**
 * Fill two-dimensional matrix with words.
 */
public class WordsSolver {

    private final int width;
    private final int height;
    private final DLX<WordPlacement> dlx;

    private final WordsSolutionConsumer solutionConsumer;

    public WordsSolver(int width, int height) {
        this.width = width;
        this.height = height;
        this.dlx = new LinkedDLX<>(new WordsOptionItemMapper());
        this.solutionConsumer = new WordsSolutionConsumer(width, height);
    }

    public void addWord(String word) {
        if (word.length() == width) {
            for (int row = 0; row < height; row++) {
                dlx.addOption(WordPlacement.horizontal(row, word));
            }
        }

        if (word.length() == height) {
            for (int col = 0; col < width; col++) {
                dlx.addOption(WordPlacement.vertical(col, word));
            }
        }
    }

    public void solve() {
        dlx.search(solutionConsumer, solutionConsumer::isSolutionFound);
    }

    public char[] getSolution() {
        return solutionConsumer.getSolution();
    }
}
