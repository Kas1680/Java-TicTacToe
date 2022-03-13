

import java.io.IOException;
import java.net.Socket;
import java.util.Scanner;

public class TicTacToeClient {
    private Socket client;
    private int playerID;
    private boolean isQuit;
    private Connection connectedServer;
    public static final int PORT = 22222;
    private Scanner scan;

    public TicTacToeClient(Socket client){
        this.client = client;
        this.isQuit = false;
        this.scan = new Scanner(System.in);
    }


    // PSVM
    public static void main(String[] args){
        String HOST = args.length > 0 ? args[0] : "localhost";


        try(Socket client = new Socket(HOST, PORT)){
            new TicTacToeClient(client).connect();
        }
        catch(IOException e){
            System.out.println("Cannot connect to server");
        }
    }

    private void connect(){
        try {
            this.connectedServer = new Connection(this.client);
            System.out.println("Connected to server");
            while (!isQuit) {
                int command = this.connectedServer.readInt();
                // Reactive handle are handled here
                switch (command) {
                    case TicTacToeProtocal.PLAYER:
                        this.handlePlayer();
                        break;
                    case TicTacToeProtocal.TURN:
                        this.handleTurn();
                        break;
                    case TicTacToeProtocal.GAMEEND:
                        this.handleGameEnd();
                        break;
                    case TicTacToeProtocal.QUIT: // activate finally block
                        this.handleQuit();
                        break;
                }
            }
        } catch (IOException e) {
            System.out.println("Unable to communicate with server");
        } finally{ // Game quit, close everything
            this.connectedServer.close();
        }
    }



    private void handleTurn() throws IOException{
        // 1. First Print the current game status, make new because player only need printboard()
        int i = this.connectedServer.readInt();
        System.out.printf("Player %d. It is currently your turn\n", i);
        new GameBoard(this.connectedServer.readMessage()).printBoard();
        boolean requestCommand = true;
        // 2. Wait for player command here

        while(requestCommand) {
            System.out.println("> ");
            String[] s = this.scan.nextLine().split(" ");// <<-- player command

            // Break down the command to its components
            if (s.length == 1 && s[0].equals("quit")) { // if quit
                sendQuit();
                requestCommand = false;
            } else if (s.length == 3 && s[0].equals("move")) { // if move
                try {
                    int Row = Integer.parseInt(s[1]);
                    int Col = Integer.parseInt(s[2]);

                    // Check for Row/Col between 0-2 inclusive
                    if (Row >= 0 && Row <= 2 && Col >= 0 && Col <= 2) {
                        sendMove(Row, Col);
                        requestCommand = false;
                    } else {
                        System.out.println("Invalid Move. Please try again");
                    }
                } catch (NumberFormatException ex) {
                    System.out.println("Invalid Input. Please try again");
                }
            }else{
                System.out.println("Invalid Command. Please try again");
            }


        }
    }

    private void handleGameEnd() throws IOException{
        int winner = this.connectedServer.readInt();
        if(winner == 0){
            System.out.println("Game Over! The game has tied");
        }else if(winner == playerID){
            System.out.println("You won the game!");
        }else{
            System.out.printf("Game Over! Player %d won", winner);
        }
        System.out.println();
    }

    private void handlePlayer() throws IOException{
        this.playerID = this.connectedServer.readInt();
    }

    private void handleQuit(){
        this.isQuit = true;
    }

    private void sendQuit() throws IOException{
        this.connectedServer.writeInt(TicTacToeProtocal.QUIT);
        this.isQuit = true;
    }

    private void sendMove(int Row, int Col) throws IOException{
        this.connectedServer.writeInt(TicTacToeProtocal.MOVE);
        this.connectedServer.writeInt(Row);
        this.connectedServer.writeInt(Col);
    }
}
