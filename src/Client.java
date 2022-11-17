import java.io.*;
import java.net.Socket;
import java.util.Scanner;

public class Client {

    private Socket socket;
    private BufferedReader bufferedReader;
    private BufferedWriter bufferedWriter;
    private String username ;

    public Client(Socket socket, String username) {
        try{
            this.socket = socket;
            this.username = username;
            this.bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            this.bufferedWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            this.username = username;

        }catch (IOException e)
        {
            CloseAll(socket , bufferedWriter , bufferedReader);
        }
    }
public void sendMessage(){

    try{
        bufferedWriter.write(username);
        bufferedWriter.newLine();
        bufferedWriter.flush();
        Scanner scanner = new Scanner(System.in);
        while (socket.isConnected())
        {
            String messageToSend = scanner.nextLine();
            bufferedWriter.write(username + ":" + messageToSend);
            bufferedWriter.newLine();
            bufferedWriter.flush();

        }


    }
    catch (IOException e){

        CloseAll(socket , bufferedWriter , bufferedReader);
    }
}

    // this method contains everything that will run on separate thread
    public void MessageListener(){

        new Thread(new Runnable() {
            @Override
            public void run() {
                String msgFromChat ;


                while (socket.isConnected())
                {
                    try{

                        msgFromChat = bufferedReader.readLine();
                        System.out.println(msgFromChat);
                    }
                    catch (IOException e ){
                        CloseAll(socket , bufferedWriter , bufferedReader);
                    }

                }
            }
        }).start();


    }


    public void CloseAll(Socket socket , BufferedWriter bufferedWriter , BufferedReader bufferedReader)
    {

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

    public static void main(String[] args) throws IOException {
        Scanner scanner = new Scanner(System.in);
        System.out.println("please tell us your name");
        String userame = scanner.nextLine();
        Socket socket = new Socket("localhost" , 1234);
        Client client = new Client(socket , userame);
        client.MessageListener();
        client.sendMessage();
    }


}
