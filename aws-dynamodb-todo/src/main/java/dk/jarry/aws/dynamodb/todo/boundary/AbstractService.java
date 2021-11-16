package dk.jarry.aws.dynamodb.todo.boundary;

import java.util.HashMap;
import java.util.Map;

import dk.jarry.aws.dynamodb.todo.entity.ToDo;
import software.amazon.awssdk.services.dynamodb.model.AttributeAction;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;
import software.amazon.awssdk.services.dynamodb.model.AttributeValueUpdate;
import software.amazon.awssdk.services.dynamodb.model.DeleteItemRequest;
import software.amazon.awssdk.services.dynamodb.model.GetItemRequest;
import software.amazon.awssdk.services.dynamodb.model.PutItemRequest;
import software.amazon.awssdk.services.dynamodb.model.ScanRequest;
import software.amazon.awssdk.services.dynamodb.model.UpdateItemRequest;

public abstract class AbstractService {

	public final static String TODO_UUID_COL = "uuid";
	public final static String TODO_SUBJECT_COL = "subject";
	public final static String TODO_BODY_COL = "body";

	public String getTableName() {
		return "ToDos";
	}

	protected ScanRequest getScanRequest() {
		return ScanRequest.builder() //
				.tableName(getTableName()) //
				.attributesToGet( //
						TODO_UUID_COL, //
						TODO_SUBJECT_COL, //
						TODO_BODY_COL //
				).build();
	}

	/**
	 * Create
	 * 
	 * @param toDo
	 * @return
	 */
	protected PutItemRequest getPutItemRequest(ToDo toDo) {

		Map<String, AttributeValue> item = new HashMap<>();
		item.put(TODO_UUID_COL, AttributeValue.builder().s(toDo.getUuid()).build());
		item.put(TODO_SUBJECT_COL, AttributeValue.builder().s(toDo.getSubject()).build());
		item.put(TODO_BODY_COL, AttributeValue.builder().s(toDo.getBody()).build());

		return PutItemRequest.builder() //
				.tableName(getTableName()) //
				.item(item) //
				.build();
	}

	/**
	 * Read
	 * 
	 * @param uuid
	 * @return
	 */
	protected GetItemRequest getGetItemRequest(String uuid) {

		Map<String, AttributeValue> key = new HashMap<>();
		key.put(TODO_UUID_COL, AttributeValue.builder().s(uuid).build());

		return GetItemRequest.builder() //
				.tableName(getTableName()) //
				.key(key) //
				.attributesToGet( //
						TODO_UUID_COL, //
						TODO_SUBJECT_COL, //
						TODO_BODY_COL //
				).build();
	}

	protected UpdateItemRequest getUpdateItemRequest(String uuid, ToDo toDo) {

		Map<String, AttributeValue> key = new HashMap<>();
		key.put(TODO_UUID_COL, AttributeValue.builder().s(uuid).build());

		Map<String, AttributeValueUpdate> updatedValues = new HashMap<>();
		updatedValues.put(TODO_SUBJECT_COL, //
				AttributeValueUpdate.builder().value(AttributeValue.builder().s(toDo.getSubject()).build())
						.action(AttributeAction.PUT).build());
		updatedValues.put(TODO_BODY_COL, //
				AttributeValueUpdate.builder().value(AttributeValue.builder().s(toDo.getBody()).build())
						.action(AttributeAction.PUT).build());

		return UpdateItemRequest.builder() //
				.tableName(getTableName()) //
				.key(key) //
				.attributeUpdates(updatedValues) //
				.build();
	}

	protected DeleteItemRequest getDeleteItemRequest(String uuid) {

		Map<String, AttributeValue> key = new HashMap<>();

		key.put(TODO_UUID_COL, AttributeValue.builder().s(uuid).build());

		return DeleteItemRequest.builder() //
				.tableName(getTableName()) //
				.key(key) //
				.build();
	}

}
