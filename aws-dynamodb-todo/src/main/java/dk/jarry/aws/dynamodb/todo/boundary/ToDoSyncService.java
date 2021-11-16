package dk.jarry.aws.dynamodb.todo.boundary;

import java.util.List;
import java.util.stream.Collectors;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import dk.jarry.aws.dynamodb.todo.entity.ToDo;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;

@ApplicationScoped
public class ToDoSyncService extends AbstractService {

	@Inject
	DynamoDbClient dynamoDB;

	public List<ToDo> findAll() {
		return dynamoDB.scanPaginator(scanRequest()).items().stream() //
				.map(ToDo::from) //
				.collect(Collectors.toList());
	}

	public List<ToDo> add(ToDo toDo) {
		dynamoDB.putItem(putRequest(toDo));
		return findAll();
	}

	public ToDo get(String uuid) {
		return ToDo.from(dynamoDB.getItem(getRequest(uuid)).item());
	}

}
