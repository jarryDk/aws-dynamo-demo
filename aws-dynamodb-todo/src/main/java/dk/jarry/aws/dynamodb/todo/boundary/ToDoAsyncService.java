package dk.jarry.aws.dynamodb.todo.boundary;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;

import dk.jarry.aws.dynamodb.todo.entity.ToDo;
import io.smallrye.mutiny.Uni;
import software.amazon.awssdk.services.dynamodb.DynamoDbAsyncClient;
import software.amazon.awssdk.services.dynamodb.model.DeleteItemResponse;
import software.amazon.awssdk.services.dynamodb.model.PutItemResponse;

@ApplicationScoped
public class ToDoAsyncService extends AbstractService {

	@Inject
	DynamoDbAsyncClient dynamoDB;

	/**
	 * Create a new ToDo
	 * 
	 * @param toDo
	 * @return Uni<ToDo>
	 */
	public Uni<ToDo> create(ToDo toDo) {
		String uuid = UUID.randomUUID().toString();
		toDo.setUuid(uuid);
		ZonedDateTime now = ZonedDateTime.now();
		toDo.setCreatedAt(now);
		toDo.setUpdatedAt(now);

		try {
			CompletableFuture<PutItemResponse> future = dynamoDB.putItem(getPutItemRequest(toDo));
			future.get();
			return Uni.createFrom()
				.completionStage(() -> dynamoDB.getItem(getGetItemRequest(uuid))) //
				.onItem()
				.transform(resp -> ToDo.from(resp.item()));
		} catch (ExecutionException | InterruptedException e) {
			throw new WebApplicationException( //
					"Not able to create ToDo", //
					Response.Status.INTERNAL_SERVER_ERROR);
		}

		/*
		How is should be - but the CompletableFuture have empty attributes !
		return Uni.createFrom()
				.completionStage(() -> dynamoDB.putItem(getPutItemRequest(toDo))) //
				.onItem()
				.transform(resp -> ToDo.from(resp.attributes()));
		*/

	}

	/**
	 * Read a toDo based on uuid
	 * 
	 * @param uuid
	 * @return Uni<ToDo>
	 * @throws WebApplicationException if toDo not found
	 */
	public Uni<ToDo> read(@dk.jarry.aws.dynamodb.todo.control.UUID String uuid) {
		return Uni.createFrom()
				.completionStage(() -> dynamoDB.getItem(getGetItemRequest(uuid))) //
				.onItem().invoke(resp -> { //
					if (resp.item().size() == 0) { //
						throw new WebApplicationException( //
								"ToDo with uuid of " + uuid + " does not exist.", //
								Response.Status.NOT_FOUND);
					}
				})
				.onItem()
				.transform(resp -> ToDo.from(resp.item()));
	}

	/**
	 * Update a toDo based on uuid
	 * 
	 * @param uuid
	 * @param toDo
	 * @return Uni<ToDo>
	 * @throws WebApplicationException if toDo not found
	 */
	public Uni<ToDo> update(@dk.jarry.aws.dynamodb.todo.control.UUID String uuid, ToDo toDo) {
		ZonedDateTime now = ZonedDateTime.now();
		toDo.setUpdatedAt(now);

		return Uni.createFrom().completionStage( //
				() -> dynamoDB.updateItem(getUpdateItemRequest(uuid, toDo))) //
				.onItem().invoke(resp -> { //
					if (resp.attributes().size() == 0) { //
						throw new WebApplicationException( //
								"ToDo with uuid of " + uuid + " does not exist.", //
								Response.Status.NOT_FOUND);
					}
				})
				.onItem() //
				.transform(resp -> ToDo.from(resp.attributes()));
	}

	public void delete(@dk.jarry.aws.dynamodb.todo.control.UUID String uuid) {
		try {
			CompletableFuture<DeleteItemResponse> future = dynamoDB.deleteItem(getDeleteItemRequest(uuid));
			DeleteItemResponse deleteItemResponse = future.get();
			if (deleteItemResponse.attributes().size() == 0) {
				throw new WebApplicationException( //
						"ToDo with uuid of " + uuid + " does not exist.", //
						Response.Status.NOT_FOUND);
			}
		} catch (ExecutionException | InterruptedException e) {
			throw new WebApplicationException( //
					"Not able to delete ToDo with uuid " + uuid, //
					Response.Status.INTERNAL_SERVER_ERROR);
		}
	}

	public Uni<List<ToDo>> findAll() {
		return Uni.createFrom().completionStage( //
				() -> dynamoDB.scan(getScanRequest())) //
				.onItem() //
				.transform( //
						res -> res.items() //
								.stream() //
								.map(ToDo::from) //
								.collect(Collectors.toList()));
	}

}
