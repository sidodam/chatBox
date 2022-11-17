import java.io.*;
import java.net.Socket;
import java.util.ArrayList;

public class ClientHandler implements Runnable {

    public static ArrayList<ClientHandler> clientHandlers = new ArrayList<>();
    private Socket socket;
    private BufferedReader bufferedReader;
    private BufferedWriter bufferedWriter;
    private String clientUsername ;

    public ClientHandler(Socket socket) throws IOException {
        try{

        this.socket = socket;
         this.bufferedWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
        this.bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
         this.clientUsername = bufferedReader.readLine();
         clientHandlers.add(this);
         broadcastMessage("SERVER: " + clientUsername + " has joined the chat" );
        }
        catch (IOException e){
            CloseAll(socket , bufferedWriter , bufferedReader);

        }

    }
// this method contains everything that will run on separate thread
 //what we gonna do is listen to new messages which is blocking operation
    @Override
    public void run() {
        String clientMsg ;

        while(socket.isConnected())
        {

            try{
                clientMsg = bufferedReader.readLine(); // blocking operation
                broadcastMessage(clientMsg);


            }
            catch (IOException e){
                CloseAll(socket , bufferedWriter , bufferedReader);
                break;
            }

        }



    }
    public void broadcastMessage(String message)
    {
         for (ClientHandler clientHandler : clientHandlers)
         {

             try{

                     clientHandler.bufferedWriter.write(message);
                     clientHandler.bufferedWriter.newLine();
                     clientHandler.bufferedWriter.flush();

             }
             catch (IOException e){
                 CloseAll(socket , bufferedWriter , bufferedReader);
             }

         }

    }

    //if user has left the chat
    public void removeClientHandler(){
        clientHandlers.remove(this); // remove current client , if he left chat no need to send him
        broadcastMessage("SERVER: " + clientUsername + " has left the chat" );

    }

    public void CloseAll(Socket socket , BufferedWriter bufferedWriter , BufferedReader bufferedReader)
    {
            removeClientHandler();

        try{
            if (bufferedReader != null)
            {
                bufferedReader.close();
            }
            if (bufferedWriter !=null)
            {
                bufferedWriter.close();
            }

            if (socket != null)
            {
                socket.close();
            }

        }
        catch (IOException e){
       e.printStackTrace();
        }

    }

}
