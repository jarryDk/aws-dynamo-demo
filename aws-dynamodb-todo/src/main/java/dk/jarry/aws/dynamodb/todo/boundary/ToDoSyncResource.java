package dk.jarry.aws.dynamodb.todo.boundary;

import java.util.List;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;

import dk.jarry.aws.dynamodb.todo.entity.ToDo;

@Path("/todos")
public class ToDoSyncResource {

	@Inject
	ToDoSyncService service;

	@GET
	public List<ToDo> getAll() {
		return service.findAll();
	}

	@GET
	@Path("{uuid}")
	public ToDo getSingle(@PathParam("uuid") String uuid) {
		return service.get(uuid);
	}

	@POST
	public List<ToDo> add(ToDo toDo) {
		service.add(toDo);
		return getAll();
	}

}