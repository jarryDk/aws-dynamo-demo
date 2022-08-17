package dk.jarry.aws;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import javax.inject.Inject;
import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;

import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.jboss.logging.Logger;
import org.junit.jupiter.api.Test;

import dk.jarry.todo.control.ToDoAsyncResourceClient;
import io.quarkus.test.junit.QuarkusTest;

@QuarkusTest
public class ToDoAsyncResourceTest {

    public static final Logger LOG = Logger.getLogger(ToDoAsyncResourceTest.class);

    @Inject
    @RestClient
    ToDoAsyncResourceClient resourceClient;

    @Test
    public void create() {

        JsonObjectBuilder createObjectBuilder = Json.createObjectBuilder();
        createObjectBuilder.add("subject", "Subject - test");
        createObjectBuilder.add("body", "Body - test");
        JsonObject todoInput = createObjectBuilder.build();

        LOG.info("todoInput : " + todoInput);
        System.out.println("todoInput : " + todoInput);

        var todoOutput = this.resourceClient.create(todoInput);

        LOG.info("todoOutput : " + todoOutput);
        System.out.println("todoOutput : " + todoOutput);

        assertEquals(todoInput.getString("subject"), todoOutput.getString("subject"));
        assertEquals(todoInput.getString("body"), todoOutput.getString("body"));

        System.out.println("create - Todo " + todoOutput);

    }

    @Test
    public void read() {

        JsonObjectBuilder createObjectBuilder = Json.createObjectBuilder();
        createObjectBuilder.add("subject", "Subject - test");
        createObjectBuilder.add("body", "Body - test");

        JsonObject todoInput = createObjectBuilder.build();
        var todoOutput = this.resourceClient.create(todoInput);

        assertEquals(todoInput.getString("subject"), todoOutput.getString("subject"));
        assertEquals(todoInput.getString("body"), todoOutput.getString("body"));

        System.out.println("read - Todo [1] " + todoOutput);

        String uuid = todoOutput.getString("uuid");

        todoOutput = this.resourceClient.read(uuid);

        assertEquals(todoInput.getString("subject"), todoOutput.getString("subject"));
        assertEquals(todoInput.getString("body"), todoOutput.getString("body"));

        System.out.println("read - Todo [2] " + todoOutput);

    }

    @Test
    public void update() {

        JsonObjectBuilder createObjectBuilder = Json.createObjectBuilder();
        createObjectBuilder.add("subject", "Subject - test");
        createObjectBuilder.add("body", "Body - test");

        JsonObject todoInput = createObjectBuilder.build();
        var todoOutput = this.resourceClient.create(todoInput);

        assertEquals(todoInput.getString("subject"), todoOutput.getString("subject"));
        assertEquals(todoInput.getString("body"), todoOutput.getString("body"));

        System.out.println("update - Todo [1] " + todoOutput);

        String uuid = todoOutput.getString("uuid");

        JsonObjectBuilder todoUpdateBuilder = Json.createObjectBuilder(todoOutput);
        todoUpdateBuilder.add("subject", "new subject");
        var todoUpdated = todoUpdateBuilder.build();

        todoOutput = this.resourceClient.update(uuid, todoUpdated);

        assertEquals(todoUpdated.getString("subject"), "new subject");
        assertEquals(todoInput.getString("body"), todoOutput.getString("body"));

        System.out.println("update - Todo [2] " + todoOutput);

    }

    @Test
    public void delete() {

        JsonObjectBuilder createObjectBuilder = Json.createObjectBuilder();
        createObjectBuilder.add("subject", "Subject - test");
        createObjectBuilder.add("body", "Body - test");

        JsonObject todoInput = createObjectBuilder.build();
        var todoOutput = this.resourceClient.create(todoInput);

        assertEquals(todoInput.getString("subject"), todoOutput.getString("subject"));
        assertEquals(todoInput.getString("body"), todoOutput.getString("body"));

        System.out.println("delete- Todo " + todoOutput);

        String uuid = todoOutput.getString("uuid");

        this.resourceClient.delete(uuid);

        try{
            todoOutput = this.resourceClient.read(uuid);
        } catch (javax.ws.rs.WebApplicationException we){
            assertTrue(we.getResponse().getStatus() == 404);
        }

    }

}