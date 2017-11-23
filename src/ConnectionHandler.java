import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.Calendar;


/**
 * Class ConnectionHandler gere todas as conexoes que contem requests
 * By extending to Thread, this class becomes a Thread*/
public class ConnectionHandler extends Thread {

    private ServerSocket theServer;
    private Socket socket;
    //private DataOutputStream out;    //Manda para fora
    private DataOutputStream out;    //Manda para fora
    private BufferedReader in;       //Recebe do Request
    String line;

    /**
     * Construtor da Classe!
     *
     * @param theServer este parametro receberá a instancia do servidor.
     *  ServerSocket é responsável por esperar a conexão do cliente.
     *  Esta classe possui um construtor onde passamos a porta que desejamos usar para escutar as conexões.
     *
     * @throws Exception A exceção que é lançada quando ocorre um erro.
     */

    public ConnectionHandler(ServerSocket theServer) throws Exception {
        this.theServer = theServer;
    }

    /**
     * Override
     * Thread class contains a method run which is call automatically when we start the thread
     */
    @Override
    public void run() {


        while (true) {
            // Blocks until receive a new connection

            Socket sock = null;
            try {
                sock = theServer.accept();
            } catch (IOException e) {

            }

            try {

                if(sock!=null)
                    Connectionhandler2(sock);

                //Se a String não for nula, ou seja, há pedido ele vai processar o pedido e a resposta chamando a classe HTTPResponse
                if (this.line != null && sock!=null)
                    new HTTPResponse(this.line, this.out, this.in);

                out.close();
                in.close();
                socket.close();

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * O Browser manda um Http Request ao servidor que vai ser lido com um BufferedReader.
     * O uso do BufferedReader porque o browser manda varias linhas.
     *
      * @param socket este parametro recebe o cliente, Ao contrário da classe ServerSocket que funciona como um Servidor escutando o cliente,
     *          a classe Socket é o cliente propriamente dito.
     * @throws Exception A exceção que é lançada quando ocorre um erro.
     */
    public void Connectionhandler2 (Socket socket) throws Exception{

        this.socket = socket;
        System.out.println("Server ready: " + socket);
        in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        out = new DataOutputStream(socket.getOutputStream());

        /*Recebe a 1º linha do Request GET / HTTP/1.1 ->  Tipo GET / PATH / http Version*/
        line = in.readLine();

        Calendar cal = Calendar.getInstance();
        cal.getTime();
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
        String time = "[" + sdf.format(cal.getTime()) + "] ";
        System.out.print(time + this.socket.getInetAddress().toString() + " " + this.line + "\n");
        /*PRINT O REQUEST inteiro
        //here we get the request String
        while (in.ready() || reqS.length() == 0)
            reqS += (char) in.read();
        System.out.println(reqS);
         */
    }
}