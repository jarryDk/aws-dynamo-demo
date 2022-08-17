package dk.jarry.aws.dynamodb.todo.boundary;

import java.util.List;

import javax.inject.Inject;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.eclipse.microprofile.metrics.MetricUnits;
import org.eclipse.microprofile.metrics.annotation.Counted;
import org.eclipse.microprofile.metrics.annotation.Timed;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

import dk.jarry.aws.dynamodb.todo.entity.ToDo;
import io.smallrye.mutiny.Uni;

/*
 * https://github.com/quarkusio/quarkus/issues/21492
 */

@Path("/async-todos")
@Tag(name = "Todo Resource - async", description = "All Todo Operations")
public class ToDoAsyncResource {

	@Inject
	ToDoAsyncService service;

	/**
	 * Create a new ToDo
	 * @param toDo
	 * @return toDo
	 */
	@Operation(description = "Create a new todo")
	@Counted(name = "createPerformedAsync", description = "How many create have been performed.")
	@Timed(name = "createTimerAsync", description = "A measure of how long it takes to perform the primality test.", unit = MetricUnits.MILLISECONDS)
	@POST
	public Uni<ToDo> create(ToDo toDo) {
		return service.create(toDo);
	}

	/**
	 * Read a toDo based on uuid
	 * @param uuid
	 * @return toDo
	 */
	@Operation(description = "Get a specific todo by uuid")
	@GET
	@Path("{uuid}")
	public Uni<ToDo> read(@PathParam("uuid") String uuid) {
		return service.read(uuid);
	}

	/**
	 * Update a toDo based on uuid
	 * @param uuid
	 * @param toDo
	 * @return toDo
	 */
	@Operation(description = "Update an exiting todo")
	@PUT
	@Path("{uuid}")
	public Uni<ToDo> update(@PathParam("uuid") String uuid, ToDo toDo) {
		return service.update(uuid, toDo);
	}

	/**
	 * Delete a toDo based on uuid
	 * @param uuid
	 */
	@Operation(description = "Delete a specific todo by uuid")
	@DELETE
	@Path("{uuid}")
	public void delete(@PathParam("uuid") String uuid) {
		service.delete(uuid);
	}

	/**
	 * Read all todos
	 * @return
	 */
	@Operation(description = "Get all the todos")
	@GET
	public Uni<List<ToDo>> getAll() {
		return service.findAll();
	}

	/**
	 * Get the name of the DynamoDb table
	 * @return tablename
	 */
	@Operation(description = "Get tablename")
	@GET
	@Path("tablename")
	@Produces(MediaType.TEXT_PLAIN)
	public String getTableName() {
		return service.getTableName();
	}

}