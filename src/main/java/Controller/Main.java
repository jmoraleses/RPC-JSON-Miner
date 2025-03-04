package Controller;

import Model.Block;
import Model.Coinbase;
import Util.Util;

import java.io.*;
import java.lang.reflect.Array;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Base64;
import java.util.concurrent.TimeUnit;

import static Core.ScryptHelper.printByteArray;

/**
 * (SHA256) Bitcoin
 */
public class Main {

    public static double fee_for_mine = 6.25;
//    public static String publicKey = "0323b0e585dbf9b78a6139c362b07a28faeef46026b65bdabb5a3fe1da72de8b1d"; //example public key: dogecoin
    public static String publicKey = "03d6c65b78abe027a7db57c1891142391115aa3c1ed50172991ae5adb75a83c226"; //example public key: bitcoin testnet
    //public static String publicKey = "zs1cy86um4azk6rmwdq5gsdljpuht2g2k54f3m5allagn9wer6t9qcaakh0435l8kwvzlpe7k8c03x"; //example public key: Zcash testnet


    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws Throwable {

        System.out.println("##############################");
        System.out.println("## Welcome to Breaking Hash ##");
        System.out.println("##############################");

        //json for to get template for mining in bitcoin 0.22.0
        String request = "{\"jsonrpc\": \"2.0\", \"id\": \"curltest\", \"method\": \"getblocktemplate\", \"params\": [{\"rules\": [\"segwit\"]}]}";
        Block blockMined = null;
        String response = "";

        while(blockMined == null){
            long startTime = System.currentTimeMillis();
            response = sendRequest(request);
            if (!response.equals("")) {
                blockMined = Mining.mining(response, startTime, "sha256"); //Busca el blochash dado los parametros
                TimeUnit.SECONDS.sleep(1);
            }
        }

        if (blockMined.getNonce() != null){

            Coinbase coinbase = new Coinbase();
            coinbase.set(blockMined); //configura el header (coinbase transaction)
            coinbase.setTransactionMineds(Util.transactionToList(blockMined.getTransactions())); //guarda las transacciones de JSONArray a List<Transaction>

            System.out.println();
//                System.out.println("Coinbase: "+coinbase.transactiontoJSON());
            System.out.println("Merkleroot: "+blockMined.getMerkleRoot());
//                System.out.println("Transactions: "+coinbase.getTransactionMineds());
            System.out.println(blockMined.toString());
            System.out.println();

            String blockMinedString =  blockMined.showBlock() + coinbase.showTransaction() ; //muestra la información del bloque y la información de la transaccion coinbase

            String request2 = "{\"jsonrpc\": \"1.0\", \"id\": \"curltest\", \"method\": \"submitblock\", \"params\": [" + blockMinedString + "]}";
            System.out.println(request2);

            String response2 = sendRequest(request2);
            System.out.println(response2);

//                System.out.println("MerkleRoot: "+blockMined.getMerkleRoot());
//                System.out.println(Util.checkMerkleRoot(blockMined.getMerkleRoot(), coinbase.getTransactionMineds() ));
            System.out.println("Blockhash: "+blockMined.getBlockhash());

        }

    }

    /**
     * Send request to bitcoin node
     * @param request
     * @return
     * @throws Throwable
     */
    //Function to Connect to node bitcoin
    public static String sendRequest(String request) throws Throwable {
        //Authentication in bitcoin node
        String userpass = "prueba:prueba";
        String basicAuth = "Basic " + new String(Base64.getEncoder().encode(userpass.getBytes()));
        URL url = new URL("http://127.0.0.1:18332/");
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("POST");
        conn.setRequestProperty("Authorization", basicAuth);
        conn.setRequestProperty("Content-Type", "application/json");
        conn.setDoOutput(true);
        conn.setDoInput(true);
        conn.setUseCaches(false);
        conn.setAllowUserInteraction(false);
        conn.setConnectTimeout(10000);
        conn.setReadTimeout(10000);
        conn.connect();
        DataOutputStream wr = new DataOutputStream(conn.getOutputStream());
        wr.writeBytes(request);
        wr.flush();
        wr.close();
        BufferedReader rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
        String line;
        StringBuffer response = new StringBuffer();
        while ((line = rd.readLine()) != null) {
            response.append(line);
        }
        rd.close();
        return response.toString();
    }


    /**
     * Function for send request to bitcoin server
     *
     * @param requestBody
     * @return
     */
//    public static String sendRequest(String requestBody) {
//        String uri = "http://127.0.0.1:18332";
//        String contentType = "application/json";
//        //user and password for bitcoin server
//        String user = "prueba";
//        String password = "prueba";
//        String response = "";
//        try {
//            URL url = new URL(uri);
//            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
//            connection.setDoOutput(true);
//            connection.setRequestMethod("POST");
//            connection.setRequestProperty("Content-Type", contentType);
//            connection.setRequestProperty("Accept", contentType);
//            connection.setRequestProperty("Authorization", "Basic " + Base64.getEncoder().encodeToString((user + ":" + password).getBytes()));
//            OutputStreamWriter writer = new OutputStreamWriter(connection.getOutputStream());
//            writer.write(requestBody);
//            writer.flush();
//            writer.close();
//            int responseCode = connection.getResponseCode();
//            if (responseCode == HttpURLConnection.HTTP_OK) {
//                BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
//                String inputLine;
//                while ((inputLine = in.readLine()) != null) {
//                    response += inputLine;
//                }
//                in.close();
//            } else {
//                System.out.println("Error: " + responseCode);
//            }
//
//        } catch (Exception e) {
//            System.out.println("Conexión rechazada.");
//            //e.printStackTrace();
//        }
//        return response;
//    }

}


