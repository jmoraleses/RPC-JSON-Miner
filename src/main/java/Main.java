import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Authenticator;
import java.net.HttpURLConnection;
import java.net.PasswordAuthentication;
import java.net.URL;

public class Main {

    private static JsonCreate jsonCreate;

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws Throwable {

        System.out.println("##############################");
        System.out.println("## Welcome to Breaking Hash ##");
        System.out.println("##############################");

        //Ask for introduce by keyboard the next variables: dir_hashcat, hash, wordlist and output_file
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        String route_hashcat = "C:/Users/Javier/Documents/hashcat-6.2.4"; //Introduce the route of hashcat

        //if route_hashcat is equal to "" then return to ask for route_hashcat""
        while (route_hashcat.equals("")) {
            System.out.println("Please introduce route of hashcat (necessary):");
            route_hashcat = br.readLine();
        }
        //if directory of route_hashcat doesn't exist, the program show error
        if(!(new java.io.File(route_hashcat).exists())){
            System.out.println("Error directory not exist, the program will close.");
            //close de program
            System.exit(0);
        }

        else{
            //if directory exist create a new directory "crackme"
            String route_crackme = route_hashcat + "/crackme";
            new java.io.File(route_crackme).mkdir();
            //Show message and put in pause the program until the user press enter
//            System.out.println("Please introduce name file for wordlist:");
//            String wordlist = br.readLine();
//            System.out.println("Please introduce name file for output file:");
//            String output_file = br.readLine();
//            System.out.println("Please introduce name file with the hashes:");
//            String hashes = br.readLine();

            String wordlist = "";
            String output_file = "";
            String hashes = "";



            //call to function hashcat in "Hashcat" class
            Hashcat.launch(route_hashcat, wordlist, output_file, hashes);

        }

        //pause the program until push enter
        System.out.println("Press enter to continue...");
        System.in.read();

        //variable "request" with json for getbalance in bitcoin
        String request = "{\"jsonrpc\":\"2.0\",\"id\":\"1\",\"method\":\"getbalance\"}";

        //variable "request" with json for getblocktemplate in bitcoin with rule segwit
        String request2 = "{\"jsonrpc\":\"2.0\",\"id\": 0, \"method\": \"getblocktemplate\", \"params\": [{\"capabilities\": [\"coinbasetxn\", \"workid\", \"coinbase/append\"]}]}";

        //variable "request" with json for getblocktemplate in bitcoin without rule segwit
        String request3 = "{\"jsonrpc\":\"2.0\",\"id\": 0, \"method\": \"getblocktemplate\", \"params\": [{\"capabilities\": [\"coinbasetxn\", \"workid\"]}]}";

        //Use the function sendRequest to get gettemplate in bitcoin
        String response = sendRequest(request);
        System.out.println(response);
    }

    /**
     * Function for send request to bitcoin server
     * @param requestBody
     * @return
     */
    public static String sendRequest(String requestBody) {
        Authenticator.setDefault(new Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication("prueba", "prueba".toCharArray());
            }
        });
        String uri = "http://127.0.0.1:9997";
        String contentType = "application/json";
        HttpURLConnection connection = null;

        try {
            URL url = new URL(uri);
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", contentType);
            connection.setRequestProperty("Content-Length", String.valueOf(requestBody.length()));
            connection.setUseCaches(false);
            connection.setDoInput(true);
            connection.setDoOutput(true);
            connection.setConnectTimeout(5000);
            connection.setReadTimeout(5000);
            connection.connect();
            OutputStream outputStream = connection.getOutputStream();
            outputStream.write(requestBody.getBytes());
            outputStream.flush();
            outputStream.close();
            InputStream inputStream = connection.getInputStream();
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
            StringBuilder stringBuilder = new StringBuilder();
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                stringBuilder.append(line);
            }
            bufferedReader.close();
            inputStream.close();
            System.out.println(stringBuilder.toString());
        }catch (IOException e) {
            e.printStackTrace();
        }
        return uri;
    }

}
