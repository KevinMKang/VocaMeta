package Tools;

import org.jboss.resteasy.client.jaxrs.ResteasyClient;
import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;
import org.jboss.resteasy.client.jaxrs.ResteasyWebTarget;

import javax.ws.rs.client.*;
import javax.ws.rs.core.Link;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import java.io.IOException;
import java.net.URI;


//Use GSON for jsons
//JAudioTagger for music

//Link link = Link.fromUri("--header \"Accept: application/json\" \"https://vocadb.net/api/songs\"").build();
//target = client.target(link).queryParam("query","Crescent Moon Rider");
/**
 * Main class.
 *
 */
public class Main {

    public static void main(String[] args) throws IOException {
      /*  Client client;
        Target target;

        client = ClientFactory.newClient();
        target = client.target("https://vocadb.net/api/songs").queryParam("query", "Crescent Moon Rider");

        Invocation.Builder test = target.request(MediaType.APPLICATION_JSON);
        Response response = test.get();
        String json = response.readEntity(String.class);
        System.out.println(json);
*/

        ResteasyClient client = new ResteasyClientBuilder().build();
        ResteasyWebTarget target = client.target("http://vocadb.net/api/songs").
                queryParam("query", "Crescent Moon Rider").
                queryParam("preferAccurateMatches","true").queryParam("lang","English");

        Response response = target.request(MediaType.APPLICATION_JSON).get();
        String value = response.readEntity(String.class);
        System.out.println(value);
        response.close();


    }
}

