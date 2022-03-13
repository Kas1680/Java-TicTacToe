
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class Connection implements AutoCloseable{
    private Socket socket;
    private DataInputStream input;
    private DataOutputStream output;


    // Establish a connection by creating a socket and its corresponding I/O channel
    public Connection(Socket socket) throws IOException{
        this.socket = socket;
        this.input = new DataInputStream(socket.getInputStream());
        this.output = new DataOutputStream(socket.getOutputStream());
    }

    public int readInt() throws IOException{
        return this.input.readInt();
    }

    public void writeInt(int i) throws IOException{
        this.output.writeInt(i);
        this.output.flush();
    }

    public String readMessage() throws IOException{
        return this.input.readUTF();
    }

    public void writeMessage(String s) throws IOException{
        this.output.writeUTF(s);
        this.output.flush();
    }

    public void close(){
        try{
            this.socket.close();
            this.input.close();
            this.output.close();
        }
        catch(IOException e){
            System.out.println("Unable to close socket");
        }
    }











}
