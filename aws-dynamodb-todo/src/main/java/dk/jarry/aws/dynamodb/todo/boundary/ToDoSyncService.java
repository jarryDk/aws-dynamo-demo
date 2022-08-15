package dk.jarry.aws.dynamodb.todo.boundary;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import dk.jarry.aws.dynamodb.todo.entity.ToDo;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;

@ApplicationScoped
public class ToDoSyncService extends AbstractService {

	@Inject
	DynamoDbClient dynamoDB;

	public ToDo create(ToDo toDo) {
		dynamoDB.putItem(getPutItemRequest(toDo));
		return toDo;
	}

	public ToDo read(String uuid) {
		Map<String, AttributeValue> item = dynamoDB.getItem(getGetItemRequest(uuid)).item();
		return ToDo.from(item);
	}

	public ToDo update(String uuid, ToDo toDo) {
		dynamoDB.updateItem(getUpdateItemRequest(uuid, toDo));
		return read(uuid);
	}

	public void delete(String uuid) {
		dynamoDB.deleteItem(getDeleteItemRequest(uuid));
	}

	public List<ToDo> findAll() {
		return dynamoDB.scanPaginator(getScanRequest()).items().stream() //
				.map(ToDo::from) //
				.collect(Collectors.toList());
	}

}
