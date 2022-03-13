public class GameBoard {
  private int[][] board;

  public GameBoard() {
    this("000000000");
  }

  private boolean isValid(char c) {
    return c == '0' || c == '1' || c == '2';
  }

  public GameBoard(String preset) throws IllegalArgumentException {
    this.board = new int[3][3];

    char[] p = preset.trim().toCharArray();
    if (p.length != 9) {
      throw new IllegalArgumentException("Invalid board (" + preset + ")");
    }

    for (int i = 0; i < 3; i++) {
      for (int j = 0; j < 3; j++) {
        int pos = i * 3 + j;
        if (this.isValid(p[pos])) {
          this.set(p[pos] - '0', i, j);
        } else {
          throw new IllegalArgumentException("Invalid board (" + preset + ")");
        }
      }
    }
  }

  public void set(int playerId, int row, int col) {
    this.board[row][col] = playerId;
  }

  private int get(int row, int col) {
    return this.board[row][col];
  }

  public boolean isOccupied(int row, int col) {
    return this.get(row, col) != 0;
  }

  public boolean hasEmpty() {
    for (int i = 0; i < 3; i++) {
      for (int j = 0; j < 3; j++) {
        if (get(i, j) == 0) {
          return true;
        }
      }
    }
    return false;
  }

  public String asString() {
    String s = "";
    for (int i = 0; i < 3; i++) {
      for (int j = 0; j < 3; j++) {
        s += this.board[i][j];
      }
    }
    return s;
  }

  public int allOccupiedAndSame(int r1, int c1, int r2, int c2, int r3, int c3) {
    boolean isSame = get(r1, c1) == get(r2, c2)
        && get(r1, c1) == get(r3, c3)
        && get(r2, c2) == get(r3, c3);

    return isSame ? get(r1, c1) : 0;
  }

  private String convertToString(int s) {
    return s == 0 ? " " : s == 1 ? "O" : "X";
  }

  public void printBoard() {
    for (int i = 0; i < 3; i++) {
      System.out.printf(" %s | %s | %s \n",
          convertToString(get(i, 0)),
          convertToString(get(i, 1)),
          convertToString(get(i, 2)));
      if (i != 2) {
        System.out.println("----------");
      }
    }
  }
}
