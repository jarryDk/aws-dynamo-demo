package dk.jarry.todo.control;

import javax.json.JsonArray;
import javax.json.JsonObject;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

@Path("async-todos")
@RegisterRestClient(baseUri = "http://localhost:8080")
public interface ToDoAsyncResourceClient {

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    JsonObject create(JsonObject toDo);

    @GET
	@Path("{uuid}")
	JsonObject read(@PathParam("uuid") String uuid);

	@PUT
	@Path("{uuid}")
	JsonObject update(@PathParam("uuid") String uuid, JsonObject toDo);

	@DELETE
	@Path("{uuid}")	
	public void delete(@PathParam("uuid") String uuid);

	@GET
	public JsonArray list( //
		@QueryParam("from") Integer from, //
		@QueryParam("limit") Integer limit) ;
}
