import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

public class SmsEngine  {

    private ServerSocket serverSocket;
    private  ObjectInputStream objectReader;
    private  ObjectOutputStream objectWrite;
    private Socket socket;
    private BufferedReader reader;
    private BufferedWriter writer;
    private ClientDetails clientDetails;
   public void setServerSocket(int port) throws IOException {
        this.serverSocket = new ServerSocket(port);
    }
  public ServerSocket getServerSocket() throws IOException {
        return this.serverSocket;
    }
    public void setSocket(ServerSocket serverSocket) throws IOException {
        this.socket=this.serverSocket.accept();
    }
   public Socket getSocket() throws IOException {

        return  this.socket;
    }
public void setReader(InputStreamReader inputStreamReader) throws IOException {

       this.reader=new BufferedReader(inputStreamReader);


}
public  BufferedReader getReader() throws IOException {return this.reader;
}
public void setWriter (OutputStreamWriter outputStreamWriter) throws IOException {
       this.writer=new BufferedWriter(outputStreamWriter);

}

public  BufferedWriter getWriter(){

       return  this.writer;
}


    public ObjectOutputStream getObjectWrite() {
        return objectWrite;
    }

    public void setObjectWrite(ObjectOutputStream objectWrite) {
        this.objectWrite = objectWrite;
    }

    public ObjectInputStream getObjectReader() {
        return this.objectReader;
    }

    public void setObjectReader(ObjectInputStream objectReader) {
        this.objectReader = objectReader;
    }

    public ClientDetails getClientDetails() {
        return clientDetails;
    }

    public void setClientDetails(ClientDetails clientDetails) {
        this.clientDetails = clientDetails;
    }
}






