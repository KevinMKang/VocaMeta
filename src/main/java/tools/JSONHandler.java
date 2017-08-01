package tools;

import classes.Items;
import com.google.gson.Gson;
import org.jboss.resteasy.client.jaxrs.ResteasyClient;
import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;
import org.jboss.resteasy.client.jaxrs.ResteasyWebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import com.google.gson.*;

import java.util.Map;

/**
 * Created by KMFK on 2017-01-13.
 * A singleton class designed to request and parse jsons.
 */
public class JSONHandler {

    private JSONHandler(){}
    private static ResteasyClient client = new ResteasyClientBuilder().build();
    private final static String requestString = "%s//api/v1/Articles/Details";


    public static String requestJSONVocaDB(String songName, String language){
        String json;

        ResteasyWebTarget target = client.target("http://vocadb.net/api/songs")
                .queryParam("query", songName)
                .queryParam("lang",language)
                .queryParam("songTypes","Original")
                .queryParam("fields","PVs,Weblinks")
                .queryParam("user","VocaMeta");

        Response response = target.request(MediaType.APPLICATION_JSON).get();
        json = response.readEntity(String.class);
        response.close();
        return json;
    }

    public static String requestWikiGetThumbURL(String title, String base){
        ResteasyWebTarget target = client.target(String.format(requestString, base))
                .queryParam("titles", title);

        Response response = target.request(MediaType.APPLICATION_JSON).get();
        String json = response.readEntity(String.class);
        JsonObject obj = new JsonParser().parse(json).getAsJsonObject();
        JsonObject items = obj.get("items").getAsJsonObject();

        for(Map.Entry<String, JsonElement> key: items.entrySet()){
            JsonObject songInfo = key.getValue().getAsJsonObject();
            if(songInfo.get("abstract").getAsString().toLowerCase().indexOf("redirect")==0){
                String abstractText = songInfo.get("abstract").getAsString();
                String newTitle = abstractText.substring("redirect".length());
                return requestWikiGetThumbURL(newTitle, base);
            }



            return songInfo.get("thumbnail").getAsString();
        }

        return "";


    }

    public static Items parseJSONVocaDB(String json){
        Items items;
        Gson gson = new Gson();
        items = gson.fromJson(json,Items.class);

        return items;
    }
}
