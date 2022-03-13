import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

public class TicTacToeServer {
    private ServerSocket serverSocket;
    public static final int PORT = 22222;
    private Boolean isQuit;
    private TicTacToeGame game;
    private Connection connectedPlayer;
    private int currentPlayer;
    private int startPlayer;
    private Connection p1;
    private Connection p2;


    public static void main(String[] args) {
        try{
            // Create serverSoc and start the server
            ServerSocket ss = new ServerSocket(PORT);
            System.out.println("Server Launched");
            new TicTacToeServer(ss).startServer();

        }catch (IOException e){
            System.out.println("Unable to create server");
            System.out.println(e); // print the error
        }
    }

    // Always start the server with the first connected player having first move
    private TicTacToeServer(ServerSocket serverSocket) {
        this.serverSocket = serverSocket;
        this.isQuit = false;
        this.currentPlayer = 1;
        this.startPlayer = 1;
    }


    private void startServer() throws IOException {
        // Wait for players to join
        try (
                Socket s1 = serverSocket.accept();
                Socket s2 = serverSocket.accept();

        ) { // Players joinned, add them to playerlist
            this.p1 = new Connection(s1);
            System.out.println("Player 1 connected");
            this.p2 = new Connection(s2);
            System.out.println("Player 2 connected");

            // Initialize TicTacToe
            this.initializeGame();
            // Send player their ID;
            this.sendPlayerID();
            this.connectedPlayer = p1;


            while (!isQuit) {

                this.sendTurn(); // Send current player their turn
                int command = this.connectedPlayer.readInt(); // Wait for player command

                // Received player command, determine details of command
                switch (command) {
                    case TicTacToeProtocal.MOVE:
                        this.handleMove();
                        break;
                    case TicTacToeProtocal.QUIT:
                        this.handleQuit();
                        break;
                }
            }
        }catch(IOException e){
                System.out.println("Unable to accept connections");
        } finally {
            this.closeAllConnection();
        }
    }


    private void initializeGame(){
        this.game = new TicTacToeGame(this.startPlayer);
    }

    // Connection established, send each player their ID
    private void sendPlayerID() throws IOException {
        p1.writeInt(TicTacToeProtocal.PLAYER);
        p1.writeInt(1);
        p2.writeInt(TicTacToeProtocal.PLAYER);
        p2.writeInt(2);
    }




    // Send current player notification that it's their turn in the form TURN <Player#> <boardAsString>
    private void sendTurn() throws IOException{
        this.connectedPlayer.writeInt(TicTacToeProtocal.TURN); // command
        this.connectedPlayer.writeInt(this.currentPlayer); // player turn
        this.connectedPlayer.writeMessage(game.getBoard().asString()); // current game stats as a string
    }


    // Connection class handle IOE
    private void closeAllConnection(){
        p1.close();
        p2.close();
    }

    private void switchTurn(){
        this.game.flipPlayer();
        this.currentPlayer = game.getNextPlayer(); // Determine current player 1 or 2
        this.connectedPlayer = this.currentPlayer == 1? p1 : p2; // Switch connection
    }

    private void handleMove() throws IOException{
        // Determine Row and Col
        int Row = this.connectedPlayer.readInt();
        int Col = this.connectedPlayer.readInt();


        try{
            // 1) Check for illegal move. If legal, set move
            this.game.makeNextPlay(this.currentPlayer, Row, Col);
            // 2) Determine if there is a winner, a tie, or continue
            int winnerID = this.game.hasWinner(); // 0 / 1 / 2
            if(this.isOver(winnerID)){  // tie or win
                sendGameEnd(winnerID);

                // 3b) Start a new game
                // if tie, start game with same player as from first round
                // initializeGame always use this.startPlayer
                if(winnerID == 0) {
                    this.connectedPlayer = startPlayer == 1? p1 : p2;
                    this.initializeGame();
                }
                // if won, start game with loser
                else{
                    this.startPlayer = winnerID == 1 ? 2 : 1;
                    this.currentPlayer = this.startPlayer;
                    this.connectedPlayer = startPlayer == 1? p1 : p2;
                    this.initializeGame();
                }
            }
            //3a) No winner, switch turn
            else{
                this.switchTurn();
            }
        }
        catch(InvalidMoveException e){ // Invalid move repeat turn command to current player
            // Exit handlMove and go back to while loop
        }


    }

    // Winner = 1 or 2. Tie = 0
    private boolean isOver(int winnerID){
        // 1. Winner found or 2. Game Tied
        if(winnerID != 0 || (winnerID == 0 && this.game.isTie())){
            return true;
        }
        else{ // No winner
            return false;
        }
    }

    // Send each player the GAMEEND command and the ID
    private void sendGameEnd(int ID) throws IOException{
            p1.writeInt(TicTacToeProtocal.GAMEEND);
            p1.writeInt(ID);
            p2.writeInt(TicTacToeProtocal.GAMEEND);
            p2.writeInt(ID);
    }

    // Send the other player quit
    private void handleQuit() throws IOException{
        this.isQuit = true;
        Connection whoToNotify = this.currentPlayer == 1? p2 : p1;
        sendQuit(whoToNotify);
    }

    private void sendQuit(Connection whoToNotify) throws IOException{
        whoToNotify.writeInt(TicTacToeProtocal.QUIT);
    }









}
