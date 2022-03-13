public class TicTacToeGame {

  private final GameBoard gameBoard;
  private int nextPlayer;

  public TicTacToeGame(int nextPlayer) {
    this.gameBoard = new GameBoard();
    this.nextPlayer = nextPlayer;
  }

  public void flipPlayer() {
    this.nextPlayer = this.nextPlayer == 1 ? 2 : 1;
  }

  public int getNextPlayer() {
    return this.nextPlayer;
  }

  /**
   *  makeNextPlay checks if move is valid. If valid, invoke set()
   * @param playerId current player setting the move
   * @param row position where player want to make the move
   * @param col position where player want to make the move
   * @throws InvalidMoveException
   */
  public void makeNextPlay(int playerId, int row, int col) throws InvalidMoveException {
    // Check if occupied. If yes, throw exception
    if(gameBoard.isOccupied(row, col)){
      throw new InvalidMoveException();
    }else{
      gameBoard.set(playerId, row, col); // Set the board given row and col
    }
  }

  public GameBoard getBoard() {
    return this.gameBoard;
  }
  // If there is a 3 x in one of 8 combinations, a winner is determined
  // if winner is 0, no winner
  public int hasWinner() {
    int winner = 0;

    // Keep checking while winner is 0 until all 8 combinations are exhausted
    if(winner == 0) { // check Row conditions
      for (int r = 0; r <= 2 && winner == 0; r++) {
        winner = this.gameBoard.allOccupiedAndSame(r, 0, r, 1, r, 2);
      }
    }
    if(winner == 0){ // Check Col win conditions
      for(int c = 0; c <= 2 && winner == 0; c++){
        winner = this.gameBoard.allOccupiedAndSame(0, c, 1, c, 2, c);
      }
    }
    if(winner == 0){ // Check diagonal1
      winner = this.gameBoard.allOccupiedAndSame(0, 0, 1, 1, 2, 2);
    }

    if(winner == 0){ // Check diagonal2
      winner = this.gameBoard.allOccupiedAndSame(0, 2, 1, 1, 2, 0);
    }
    return winner;
  }

  // if there is no empty spot and a winner is not announced yet (implied by hasWinner). It is a tie.
  public boolean isTie() {
    return !this.gameBoard.hasEmpty();

  }
}
