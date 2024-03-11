package fi.iki.asb.xcc.examples.words.option;

/**
 * Option for placing a word on the board.
 * @param row Row number of first letter.
 * @param col Column number of first letter.
 * @param dir Direction of word.
 * @param word The word
 */
public record WordPlacement(
        int row,
        int col,
        Direction dir,
        String word) {

    public static WordPlacement horizontal(int row, String word) {
        return new WordPlacement(row, 0, Direction.HORIZONTAL, word);
    }

    public static WordPlacement vertical(int col, String word) {
        return new WordPlacement(0, col, Direction.VERTICAL, word);
    }
}
