package dk.jarry.aws.dynamodb.todo.boundary;

import java.util.List;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;

import dk.jarry.aws.dynamodb.todo.entity.ToDo;
import io.smallrye.mutiny.Uni;

@Path("/async-todos")
public class ToDoAsyncResource {
 
	@Inject
	ToDoAsyncService service;

	@GET
    public Uni<List<ToDo>> getAll() {
        return service.findAll();
    }

    @GET
    @Path("{uuid}")
    public Uni<ToDo> getSingle(@PathParam("uuid") String uuid) {
        return service.get(uuid);
    }

    @POST
    public Uni<List<ToDo>> add(ToDo toDo) {
        return service.add(toDo)
                .onItem().ignore().andSwitchTo(this::getAll);
    }

}