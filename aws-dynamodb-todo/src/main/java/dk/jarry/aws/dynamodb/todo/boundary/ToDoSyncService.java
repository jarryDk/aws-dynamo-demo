package dk.jarry.aws.dynamodb.todo.boundary;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;

import dk.jarry.aws.dynamodb.todo.entity.ToDo;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;
import software.amazon.awssdk.services.dynamodb.model.DeleteItemResponse;
import software.amazon.awssdk.services.dynamodb.model.GetItemResponse;
import software.amazon.awssdk.services.dynamodb.model.UpdateItemResponse;

@ApplicationScoped
public class ToDoSyncService extends AbstractService {

	@Inject
	DynamoDbClient dynamoDB;

	/**
	 * Create a new ToDo
	 * 
	 * @param toDo
	 * @return toDo
	 */
	public ToDo create(ToDo toDo) {
		toDo.setUuid(UUID.randomUUID().toString());
		ZonedDateTime now = ZonedDateTime.now();
		toDo.setCreatedAt(now);
		toDo.setUpdatedAt(now);

		dynamoDB.putItem(getPutItemRequest(toDo));
		return toDo;
	}

	/**
	 * Read a toDo based on uuid
	 * 
	 * @param uuid
	 * @return toDo
	 * @throws WebApplicationException if toDo not found
	 */
	public ToDo read(@dk.jarry.aws.dynamodb.todo.control.UUID String uuid) {
		GetItemResponse itemResponse = dynamoDB.getItem(getGetItemRequest(uuid));
		Map<String, AttributeValue> item = itemResponse.item();
		LOG.debug("Read - item.size() : " + item.size());
		if (item.size() > 0) {
			return ToDo.from(item);
		} else {
			throw new WebApplicationException( //
					"ToDo with uuid of " + uuid + " does not exist.", //
					Response.Status.NOT_FOUND);
		}
	}

	/**
	 * Update a toDo based on uuid
	 * 
	 * @param uuid
	 * @param toDo
	 * @return toDo
	 * @throws WebApplicationException if toDo not found
	 */
	public ToDo update(@dk.jarry.aws.dynamodb.todo.control.UUID String uuid, ToDo toDo) {
		ZonedDateTime now = ZonedDateTime.now();
		toDo.setUpdatedAt(now);

		UpdateItemResponse itemResponse = dynamoDB.updateItem(getUpdateItemRequest(uuid, toDo));
		Map<String, AttributeValue> item = itemResponse.attributes();
		LOG.debug("Update - item.size() : " + item.size());
		LOG.debug("Update - item.keySet() : " + item.keySet());
		if (item.size() > 0) {
			return ToDo.from(item);
		} else {
			throw new WebApplicationException( //
					"ToDo with uuid of " + uuid + " does not exist.", //
					Response.Status.NOT_FOUND);
		}
	}

	/**
	 * Delete a toDo based on uuid
	 * 
	 * @param uuid
	 * @throws WebApplicationException if toDo not found
	 */
	public void delete(@dk.jarry.aws.dynamodb.todo.control.UUID String uuid) {
		DeleteItemResponse itemResponse = dynamoDB.deleteItem(getDeleteItemRequest(uuid));
		Map<String, AttributeValue> item = itemResponse.attributes();
		LOG.debug("Delete - item.size() : " + item.size());
		if (item.size() == 0) {
			throw new WebApplicationException( //
					"ToDo with uuid of " + uuid + " does not exist.", //
					Response.Status.NOT_FOUND);
		}
	}

	public List<ToDo> findAll() {
		return dynamoDB.scanPaginator(getScanRequest()).items() //
			.stream() //
			.map(ToDo::from) //
			.collect(Collectors.toList());
	}

}
