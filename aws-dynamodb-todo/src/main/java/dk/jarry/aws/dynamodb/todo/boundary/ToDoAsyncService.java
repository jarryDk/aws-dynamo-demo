package dk.jarry.aws.dynamodb.todo.boundary;

import java.util.List;
import java.util.stream.Collectors;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import dk.jarry.aws.dynamodb.todo.entity.ToDo;
import io.smallrye.mutiny.Uni;
import software.amazon.awssdk.services.dynamodb.DynamoDbAsyncClient;

@ApplicationScoped
public class ToDoAsyncService extends AbstractService {

	@Inject
	DynamoDbAsyncClient dynamoDB;

	public Uni<List<ToDo>> findAll() {
		return Uni.createFrom().completionStage( //
				() -> dynamoDB.scan(scanRequest()))  //
				.onItem()
				.transform(res -> res.items().stream().map(ToDo::from).collect(Collectors.toList()));
	}

	public Uni<List<ToDo>> add(ToDo toDo) {
		return Uni.createFrom().completionStage( //
				() -> dynamoDB.putItem(putRequest(toDo))) //
				.onItem() //
				.ignore() //
				.andSwitchTo(this::findAll);
	}

	public Uni<ToDo> get(String uuid) {
		return Uni.createFrom().completionStage( //
				() -> dynamoDB.getItem(getRequest(uuid))) //
				.onItem() //
				.transform(resp -> ToDo.from(resp.item()));
	}

}
