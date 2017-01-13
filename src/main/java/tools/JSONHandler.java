package tools;

import classes.Items;
import com.google.gson.Gson;
import com.google.gson.stream.JsonReader;
import org.jboss.resteasy.client.jaxrs.ResteasyClient;
import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;
import org.jboss.resteasy.client.jaxrs.ResteasyWebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

/**
 * Created by KMFK on 2017-01-13.
 * A singleton class designed to request and parse jsons.
 */
public class JSONHandler {

    private JSONHandler(){}

    public static String requestJSONVocaDB(String songName){
        String json;
        ResteasyClient client = new ResteasyClientBuilder().build();
        ResteasyWebTarget target = client.target("http://vocadb.net/api/songs")
                .queryParam("query", songName)
                .queryParam("preferAccurateMatches","true").queryParam("lang","English")
                .queryParam("fields","Names")
                .queryParam("preferAccurateMatches",true)
                .queryParam("songType","Original");

        Response response = target.request(MediaType.APPLICATION_JSON).get();
        json = response.readEntity(String.class);
        response.close();

        return json;
    }

    public static Items parseJSONVocaDB(String json){
        Items items = null;

        Gson gson = new Gson();

        JsonReader reader = null;
        try {
            reader = new JsonReader(new FileReader("testFiles/moonrider.json"));
            items = gson.fromJson(reader,Items.class);
            //items = gson.fromJson(json,Items.class);
            reader.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return items;

    }
}
