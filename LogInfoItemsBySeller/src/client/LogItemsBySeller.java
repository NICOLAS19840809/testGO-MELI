package client;


import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;

import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;

import java.util.Iterator;

import javax.net.ssl.HttpsURLConnection;

import org.json.JSONArray;
import org.json.JSONObject;


/**
 * Clase creada para resolver la busqueda de los items de un vendedor y retornar info de dichos items.
 */
public class LogItemsBySeller {
    public LogItemsBySeller() {
        super();
    }


    /**
     * @param parametros site_id seller_id_1 seller_id_2 ... seller_id_n
     */
    public static void main(String[] parametros) {
        
        try {
            System.out.println("EMPEZO");
            
            LogItemsBySeller logItemsBySeller = new LogItemsBySeller();

            String siteId = parametros[0];
            
            //obtener items por cada SELLER_ID
            for (int i = 1; i < parametros.length; i++){
             
              //creo un archivo para cada seller_id recibido como parametro.
              BufferedWriter writer = new BufferedWriter(logItemsBySeller.createFile(parametros[i].toString()));
                                
              //Como el servicio retorna hasta 50 resultados, se tiene que iterar hasta recorrer todos los items.
              int offset = 0;
              int quantityOfResults = 1;  
              String separator = "|";
                
              while (quantityOfResults > 0){
              
                String uriItems = "https://api.mercadolibre.com/sites/" + siteId + "/search?seller_id=" + parametros[i] + "&offset="+ offset;
                String itemsBySellerResponse = logItemsBySeller.callAPI(uriItems, "GET");
                
                JSONObject itemsBySellerResponseJSON = new JSONObject(itemsBySellerResponse);
                JSONArray results = itemsBySellerResponseJSON.getJSONArray("results");
                quantityOfResults = results.length();  
                                
                //Si obtuvo resultados parseo la info.
                if (quantityOfResults > 0 ){
                    
                  for (Iterator iterator = results.iterator(); iterator.hasNext(); ){ 
                    
                    JSONObject item = (JSONObject)iterator.next();
                    String itemId = item.getString("id");
                    String itemTitle = item.getString("title");
                    String itemCategoryId = item.getString("category_id");
                    String linea = itemId + separator + itemTitle + separator + itemCategoryId;
                         
                    //Se invoca la API de categorias para obtener el name.     
                    String uriCategory = "https://api.mercadolibre.com/categories/" + itemCategoryId;
                    String categoryResponse = logItemsBySeller.callAPI(uriCategory, "GET");
                    
                    JSONObject categoryResponseJSON = new JSONObject(categoryResponse);
                    
                    String categoryName = categoryResponseJSON.getString("name");
                    
                    linea += separator + categoryName;
                    
                    //System.out.println(linea);
                    writer.append(linea);
                    writer.newLine();
                  }  
                  offset += 50;
                }
           
              }
              writer.close();
            }
            
        } catch (MalformedURLException e) {
            System.err.println("*****MALFORMED URL EXCPETION*****");
        } catch (ProtocolException e) {
           System.err.println("*****PROTOCOL EXCEPTION*****");
        } catch (IOException e) {
           System.err.println("*****IO EXCEPTION*****");
          System.err.println(e);
        }
        System.out.println("TERMINO");
    }

    /**
     * @param uri del servicio a incocar
     * @param metodo GET, POST, PUT...
     * @return el string de respuesta del API invocada
     * @throws MalformedURLException en caso de que la URI este mal formada.
     * @throws IOException si hubo problemmas de lectura
     */
    public String callAPI(String uri, String metodo) throws MalformedURLException,
                                                      IOException {
      
      //Para agilizar el desarrollo y saltar la parte de instalacion del certificado
      System.clearProperty("javax.net.ssl.trustStore");
      System.setProperty("sun.security.ssl.allowUnsafeRenegotiation", "true");
      
      URL url = new URL(uri);
      HttpsURLConnection connection = (HttpsURLConnection) url.openConnection();
      connection.setRequestMethod(metodo);
      //Si fuese necesario usar autenticacion, hay que desarrollar la obtencion del Bearer
      //connection.setRequestProperty("Authorization", "Bearer " + "APP_USR-2156056665957909-011212-98d42f854ca186d00c74c803011d8cf0-95383816");
      connection.setRequestProperty("Content-Type", "application/json; charset=utf-8");
      connection.setRequestProperty("Accept", "application/json");

      connection.setConnectTimeout(10000);
      connection.connect();

      if (connection.getResponseCode() == 200){                    
           
        BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream()));
        StringBuilder sb = new StringBuilder();
        String line;
        while ((line = br.readLine()) != null) {
            sb.append(line+"\n");
        }
        br.close();
        return sb.toString();
      }
                      
      return "ERROR al invocar API" + connection.getResponseCode();
    }
    
    public FileWriter createFile(String name) throws IOException {
      
      new File("output").mkdir();
      String fileName = "output//sellerId-" + name + ".txt";
      System.out.println("Creo el archivo: " + fileName);
      return new FileWriter(fileName); 
    }    
}
