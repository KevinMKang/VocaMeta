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
 * A singleton class designed to request and parse jsons.
 */
public class JSONHandler {

    private JSONHandler(){}
    private static ResteasyClient client = new ResteasyClientBuilder().build();
    private final static String requestString = "%s//api/v1/Articles/Details";

    public static Items requestJSONVocaDB(String songName, String language){
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

        Items items;
        Gson gson = new Gson();
        items = gson.fromJson(json,Items.class);

        return items;
    }

    public static String requestWikiGetThumbURL(String title, String base, int width, int height){
        ResteasyWebTarget target = client.target(String.format(requestString, base))
                .queryParam("titles", title)
                .queryParam("width", width)
                .queryParam("height", height);

        Response response = target.request(MediaType.APPLICATION_JSON).get();
        String json = response.readEntity(String.class);
        response.close();
        JsonObject obj = new JsonParser().parse(json).getAsJsonObject();
        JsonObject items = obj.get("items").getAsJsonObject();

        for(Map.Entry<String, JsonElement> key: items.entrySet()){
            JsonObject songInfo = key.getValue().getAsJsonObject();
            if(songInfo.get("abstract").getAsString().toLowerCase().indexOf("redirect")==0){
                String abstractText = songInfo.get("abstract").getAsString();
                String newTitle = abstractText.substring("redirect".length());
                return requestWikiGetThumbURL(newTitle, base, 1, 1);
            }

            //String thumbnail = songInfo.get("thumbnail").getAsString();
            //thumbnail = thumbnail.substring(0, thumbnail.indexOf("latest")+"latest".length()) + thumbnail.substring(thumbnail.indexOf("?cb"));

            //return thumbnail;

            if(width==1){
                JsonObject originalDimensions = songInfo.get("original_dimensions").getAsJsonObject();
                int thumbWidth = originalDimensions.get("width").getAsInt();
                int thumbHeight = originalDimensions.get("height").getAsInt();

                int side = Math.min(thumbWidth, thumbHeight);

                return requestWikiGetThumbURL(title, base, side, side);
            }

            return songInfo.get("thumbnail").getAsString();
        }
        return "";
    }

}
