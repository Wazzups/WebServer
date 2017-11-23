import java.io.*;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.io.BufferedReader;


public class HTTPResponse {


    String formdata = "";
    // FILEPATH points to root of web server files
    private final static String FILEPATH = "www\\";

    /** Mime map
     *  HashMap is a Map based collection class that is used for storing Key & value pairs, it is denoted as HashMap<Key, Value> or HashMap<K, V>.
     */
    private static final Map<String, String> mimeMap = new HashMap<String, String>() {{
        put("html", "text/html");
        put("css", "text/css");
        put("js", "application/js");
        put("jpg", "image/jpg");
        put("jpeg", "image/jpeg");
        put("png", "image/png");
    }};

    /**
     *
     * @param code parametro que irá receber o code
     * @param mime   parametro que irá receber o mime depois de passar pelo hashmap e transformar o tipo de ficheiro no Content-Type correspondente
     * @param length parametro do content-length
     * @throws IOException A exceção que é lançada quando ocorre um erro.
     */
    private static void respondHeader(String code, String mime, int length, DataOutputStream out) throws Exception {
        System.out.println(" (" + code + ") ");
        out.writeBytes("HTTP/1.1 " + code + " OK\r\n");
        out.writeBytes("Content-Type: " + mimeMap.get(mime) + " charset=UTF-8\r\n");
        out.writeBytes("Content-Length: " + length + "\r\n");
        out.writeBytes("Server: Test Server - http://www.estgp.pt\r\n");
        out.writeBytes("\r\n");
    }

    private static void respondHeaderPost(String code, String mime, int length, DataOutputStream out, String formdata) throws Exception {
        System.out.println(" (" + code + ") ");
        out.writeBytes("HTTP/1.1 " + code + " OK\r\n");
        out.writeBytes("Content-Type: " + mimeMap.get(mime) + " charset=UTF-8\r\n");
        out.writeBytes("Content-Length: " + length + "\r\n");
        out.writeBytes("Server: Test Server - http://www.estgp.pt\r\n");
        out.writeBytes("\r\n");


        String formsplit[] = formdata.split("&");
        out.writeBytes("<html><title>Post: </title><body><h1>Post</h1><br />");

        for (String var : formsplit) {
            String s[] = var.split("=");
            out.writeBytes(s[0] + " = " + s[1] + "<br />");
        }

        out.writeBytes("</body></html>");
    }

    /**
     * Construtor para tratar do request e enviar resposta com os files processados e carregados através de bytes
     * @param inString Recebe a string que contem a 1º linha do Request para tratar
     * @param out
     * @throws Exception A exceção que é lançada quando ocorre um erro.
     */
    public HTTPResponse(String inString, DataOutputStream out, BufferedReader in) throws Exception {


        String method = inString.substring(0, inString.indexOf("/") - 1); //Tipo do Request GET-POST
        String file = inString.substring(inString.indexOf("/") + 1, inString.lastIndexOf("/") - 5); //Path do File

        // Se Tiver na raiz do local host = http://localhost:8080 ou seja apanhar a Path file vazia
        // Set default path to index.html
        if (file.equals(""))
            file = "index.html";

        String mime = file.substring(file.indexOf(".") + 1); //Vai buscar a terminação do ficheiro, o seu tipo

        // Return if trying to load file outside of web server root
        Path path = Paths.get(FILEPATH, file);
        if (!path.startsWith(FILEPATH)) {
            System.out.println(" (Dropping connection) ");
            return;
        }

        // Return if file contains potentialy bad string
        if (file.contains(";") || file.contains("*")) {
            System.out.println(" (Dropping connection)");
            return;
        }

        if (method.equals("GET")) try {
            // Open file
            byte[] fileBytes = null;
            InputStream is = new FileInputStream(FILEPATH + file);
            fileBytes = new byte[is.available()];
            is.read(fileBytes);

            /*String sim = FILEPATH + "/" + file;
            System.out.println(sim + "Correcto");*/

            // Send header response
            respondHeader("200", mime, fileBytes.length, out);

            /* Metodo alternativo de passar os Bytes
            out.write(lerbytes(sim));
            out.close();*/


            // Write content of file
            out.write(fileBytes);
            out.close();

            //Se o ficheiro não for encontrado carrega o ficheiro status404.html
        } catch(FileNotFoundException e) {
            // Try to use status404.html
            try {
                byte[] fileBytes = null;
                InputStream is = new FileInputStream(FILEPATH + "status404.html");
                fileBytes = new byte[is.available()];
                is.read(fileBytes);
                respondHeader("404", "html", fileBytes.length, out);
                out.write(fileBytes);
            } catch (FileNotFoundException e2) {
                String responseString = "404 File Not Found";
                respondHeader("404", "html", responseString.length(), out);
                out.write(responseString.getBytes());
            }
        }
        else if (method.equals("POST")) {
            byte[] fileBytes = null;
            InputStream is = new FileInputStream(FILEPATH + file);
            fileBytes = new byte[is.available()];
            char formDATA[] = new char[fileBytes.length];

            in.read(formDATA, 0, fileBytes.length);
            formdata = new String(formDATA);
           // System.out.println("RESULTS: " + formdata);
            formdata = formdata.substring(formdata.lastIndexOf("BOS_Locale=en") + 16);
            System.out.println("Resultado: " + formdata);

            respondHeaderPost("200", mime, fileBytes.length, out, formdata);
        }
    }



    /**
     * Metodo alternativo para lerbytes do ficheiro
     * !!!Não está a ser usado!!!
     * @param filename Passa o parametro do Path onde o ficheiro a ser processado está localizado
     * @return O array carregado com os bytes do ficheiro
     */
    public static byte[] lerbytes(String filename) {

        byte[] array = null;

        try {

            File file = new File(filename);
            array = Files.readAllBytes(file.toPath());


        } catch (IOException e) {
            array = lerbytes("www\\status404.html");

        }
        return array;
    }



}

