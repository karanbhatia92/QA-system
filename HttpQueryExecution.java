
import org.apache.http.ParseException;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;

/**
 * Created by Karan on 12/5/2016.
 */
public class HttpQueryExecution {
    String query;
    String ip_address = "35.165.111.84:8983";//"35.161.205.123:8983";
    String url_query = "";
    //ArrayList<String> al_entity = new ArrayList<String>();
    /*
    public HttpQueryExecution(ArrayList<String> al_entity){
        this.al_entity = al_entity;
        //this.query = query;
    }
    */

    public ArrayList<String> fireQuery(StringBuilder query, String text, ArrayList<String> objects, ArrayList<String> subjects, String root){
    	
/*        StringBuilder sb = new StringBuilder();
        for (String s : al_entities) {
            sb.append(s);
            sb.append("+");
        }*/
        url_query = "http://" + ip_address + "/solr/VSM/select?fl=*,score&indent=on&defType=dismax&q="+query+"&qf=text_en&rows=100&wt=json&";
        System.out.println("url_query: "+ url_query);
        CloseableHttpClient httpclient = HttpClients.createDefault();
        HttpGet httpget = new HttpGet(url_query);
        ArrayList<String> tweet_text = new ArrayList<String>();
        try {
            CloseableHttpResponse response = httpclient.execute(httpget);
            String json_string = EntityUtils.toString(response.getEntity(), "UTF-8");
            //JSONObject myObject = new JSONObject(json_string);
            JSONParser parser = new JSONParser();
            JSONObject obj = (JSONObject) parser.parse(json_string);
            JSONObject resp = (JSONObject) obj.get("response");
            JSONArray docs = (JSONArray) resp.get("docs");
            
            String[][] scores = new String[docs.size()][2]; 
            
            for (int j = 0; j < docs.size(); j++ ){
            	JSONObject doc = (JSONObject) docs.get(j);
            	
            	//System.out.println(doc);
            	
            	//scores[j][0] = doc.get("score").toString();
            	//String content = doc.get("text").toString();
            	//scores[j][1] = content.substring(2, content.length() - 2);
            	
            	System.out.println("OLDScore: "+ doc.get("score").toString() +" Text: "+ doc.get("text").toString());
            }
            
            for(int i=0; i<docs.size(); i++){
                JSONObject docs_iterator = (JSONObject) docs.get(i);
                
                double score1 = 0.0;
                score1 = Double.parseDouble(docs_iterator.get("score").toString());
                String content = docs_iterator.get("text").toString();
                String tweet = content.substring(2, content.length() - 2);
                
                String jsonString = docs_iterator.toString();
                
                if (text.toLowerCase().contains("who")){
                	if(jsonString.contains("PERSON")){
                		score1 = score1 + 1;
                	}
                }else if(text.toLowerCase().contains("when")){
                	if(jsonString.contains("TIME")){
                		score1 = score1 + 1;
                	}
                }else if(text.toLowerCase().contains("where")){
                	if(jsonString.contains("LOCATION") || jsonString.contains("EVENT")){
                		score1 = score1 + 1;
                	}
                }
                
                /*if (jsonString.contains(ansType)){
                	score = score + 0.5 ;
                }*/
                if (jsonString.contains("SUBJECT")){
                	Iterator<String> iterator = subjects.iterator();
                	while(iterator.hasNext()){
                		if (docs_iterator.get("SUBJECT").toString().contains(iterator.next())){
                			score1 = score1 + 1;
                		}
                	}
                }
                if (jsonString.contains("OBJECT")){
                	Iterator<String> iterator = subjects.iterator();
                	while(iterator.hasNext()){
                		if (docs_iterator.get("OBJECT").toString().contains(iterator.next())){
                			score1 = score1 + 1;
                		}
                	}
                }
                
                scores[i][0] = Double.toString(score1);
                scores[i][1] = tweet;
                
                //tweet_text.add(scores[i][1]);
            }
            Arrays.sort(scores, (a, b) -> Double.compare(Double.parseDouble(b[0]), Double.parseDouble(a[0])));
            
            for (int k = 0; k < 10; k++){
            	System.out.println("Score: "+ scores[k][0] +" Text: "+ scores[k][1]);
            	tweet_text.add(scores[k][1].replace("\\","").replace("?*", ""));
            }
            //System.out.println(docs.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        
        
        
        
        
        return tweet_text;
    }
}
