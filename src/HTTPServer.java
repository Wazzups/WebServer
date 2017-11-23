import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;

/**
 * Classe HTTPServer que contem a main(), onde esperará pelo comando "start" (CLI) para ativação do servidor.
 * Caso "start" seja usado será chamada a classe Tarefa que recebera a porta do servidor. Ficará a espera que um pedido do cliente.
 */
public class HTTPServer {

    /**
     * Thread principal main()
     * Iniciação do servidor através do comando "start" que irá chamar a classe Tarefa.
     * A classe Tarefa irá criar todas as threads necessarias para responder aos pedidos http.
     * O servidor irá para através de comando "stop"
     */
    private final static int PORT = 8080; //TCP Port number onde o network socket pode receber conexões

    public static void main(String[] args) throws Exception {

        ServerSocket theServer = null;
        Socket sock = null;
        Scanner scan = new Scanner(System.in);

        System.out.println("***CLI***");

        while (true) {

            switch (scan.next()) {

                case "start":
                    if (theServer == null) {
                        try {
                            theServer = new ServerSocket(PORT);
                            System.out.println("Server is started: " + theServer);
                            ConnectionHandler ch = new ConnectionHandler(theServer);
                            ch.start();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                    else {
                        System.out.println("Servidor já a correr");
                    }
                    break;

                case "stop":
                    if (theServer == null) {
                        System.out.println("O server ainda nao iniciou");
                        break;
                    }else {
                        try {
                            theServer.close();
                            System.out.println("Servidor parado");
                            theServer=null;

                        } catch (IOException e) {

                            e.printStackTrace();
                        }
                        break;
                    }

            }
        }
    }
}
