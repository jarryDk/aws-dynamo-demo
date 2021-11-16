package dk.jarry.aws.dynamodb.todo.boundary;

import java.util.HashMap;
import java.util.Map;

import dk.jarry.aws.dynamodb.todo.entity.ToDo;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;
import software.amazon.awssdk.services.dynamodb.model.GetItemRequest;
import software.amazon.awssdk.services.dynamodb.model.PutItemRequest;
import software.amazon.awssdk.services.dynamodb.model.ScanRequest;

public abstract class AbstractService {

	public final static String TODO_UUID_COL = "uuid";
	public final static String TODO_SUBJECT_COL = "subject";
	public final static String TODO_BODY_COL = "body";

	public String getTableName() {
		return "ToDos";
	}

	protected ScanRequest scanRequest() {
		return ScanRequest.builder() //
				.tableName(getTableName()) //
				.attributesToGet( //
						TODO_UUID_COL, //
						TODO_SUBJECT_COL, //
						TODO_BODY_COL //
				).build();
	}

	protected PutItemRequest putRequest(ToDo toDo) {
		Map<String, AttributeValue> item = new HashMap<>();
		item.put(TODO_UUID_COL, AttributeValue.builder().s(toDo.getUuid()).build());
		item.put(TODO_SUBJECT_COL, AttributeValue.builder().s(toDo.getSubject()).build());
		item.put(TODO_BODY_COL, AttributeValue.builder().s(toDo.getBody()).build());

		return PutItemRequest.builder() //
				.tableName(getTableName()) //
				.item(item) //
				.build();
	}

	protected GetItemRequest getRequest(String uuid) {
		Map<String, AttributeValue> key = new HashMap<>();
		key.put(TODO_UUID_COL, AttributeValue.builder().s(uuid).build());

		return GetItemRequest.builder().tableName(getTableName()).key(key).attributesToGet( //
				TODO_UUID_COL, //
				TODO_SUBJECT_COL, //
				TODO_BODY_COL //
		).build();
	}

}
