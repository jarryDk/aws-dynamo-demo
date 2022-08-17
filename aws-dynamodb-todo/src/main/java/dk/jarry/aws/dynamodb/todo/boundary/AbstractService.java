package dk.jarry.aws.dynamodb.todo.boundary;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

import javax.inject.Inject;

import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.jboss.logging.Logger;

import dk.jarry.aws.dynamodb.todo.entity.ToDo;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;
import software.amazon.awssdk.services.dynamodb.model.DeleteItemRequest;
import software.amazon.awssdk.services.dynamodb.model.GetItemRequest;
import software.amazon.awssdk.services.dynamodb.model.PutItemRequest;
import software.amazon.awssdk.services.dynamodb.model.ReturnValue;
import software.amazon.awssdk.services.dynamodb.model.ScanRequest;
import software.amazon.awssdk.services.dynamodb.model.UpdateItemRequest;

public abstract class AbstractService {

	public static final Logger LOG = Logger.getLogger(AbstractService.class);

	public final static String TODO_UUID_COL = "uuid";

	public final static String TODO_SUBJECT_COL = "subject";
	public final static String TODO_BODY_COL = "body";

	public final static String TODO_CREATED_AT_COL = "createdAt";
	public final static String TODO_UPDATED_AT_COL = "updatedAt";

	@Inject
	@ConfigProperty(defaultValue = "todos", name = "dynamoDbTableName")
	String dynamoDbTableName;

	public String getTableName() {
		return dynamoDbTableName;
	}

	protected ScanRequest getScanRequest() {
		return ScanRequest.builder() //
				.tableName(getTableName()) //
				.attributesToGet( //
						TODO_UUID_COL, //
						TODO_SUBJECT_COL, //
						TODO_BODY_COL, //
						TODO_CREATED_AT_COL, //
						TODO_UPDATED_AT_COL)
				.build();
	}

	/**
	 * Create
	 *
	 * @param toDo
	 * @return PutItemRequest
	 */
	protected PutItemRequest getPutItemRequest(ToDo toDo) {

		Map<String, AttributeValue> item = new HashMap<>();
		item.put(TODO_UUID_COL, AttributeValue.builder().s(
				toDo.getUuid()).build());
		item.put(TODO_SUBJECT_COL, AttributeValue.builder().s(
				toDo.getSubject()).build());
		item.put(TODO_BODY_COL, AttributeValue.builder().s(
				toDo.getBody()).build());
		item.put(TODO_CREATED_AT_COL, AttributeValue.builder().s(
				toDo.getCreatedAt().format(DateTimeFormatter.ISO_DATE_TIME)).build());
		item.put(TODO_UPDATED_AT_COL, AttributeValue.builder().s(
				toDo.getUpdatedAt().format(DateTimeFormatter.ISO_DATE_TIME)).build());

		return PutItemRequest.builder() //
				.tableName(getTableName()) //
				.item(item) //
				.build();
	}

	/**
	 * Read
	 *
	 * @param uuid
	 * @return GetItemRequest
	 */
	protected GetItemRequest getGetItemRequest(String uuid) {

		Map<String, AttributeValue> key = new HashMap<>();
		key.put(TODO_UUID_COL, AttributeValue.builder().s(uuid).build());

		return GetItemRequest.builder() //
				.tableName(getTableName()) //
				.key(key) //
				.build();
	}

	/**
	 * Update
	 * @param uuid
	 * @param toDo
	 * @return UpdateItemRequest
	 */
	protected UpdateItemRequest getUpdateItemRequest(String uuid, ToDo toDo) {

		LOG.debug("UUID : " + uuid);

		Map<String, AttributeValue> key = new HashMap<>();
		key.put(TODO_UUID_COL, AttributeValue.builder().s(uuid).build());

		Map<String, AttributeValue> items = new HashMap<>();
		StringBuilder updateExpression = new StringBuilder();
		if (toDo.getSubject() != null) {
			items.put(":" + TODO_SUBJECT_COL, AttributeValue.builder().s(toDo.getSubject()).build());
			updateExpression.append(TODO_SUBJECT_COL + " = :" + TODO_SUBJECT_COL);
		}
		if (toDo.getBody() != null) {
			items.put(":" + TODO_BODY_COL, AttributeValue.builder().s(toDo.getBody()).build());
			updateExpression.append((updateExpression.isEmpty() ? "" : ", ") + TODO_BODY_COL + " = :" + TODO_BODY_COL);
		}
		if (toDo.getUpdatedAt() != null) {
			items.put(":" + TODO_UPDATED_AT_COL, AttributeValue.builder().s(
					ZonedDateTime.now().format(DateTimeFormatter.ISO_DATE_TIME)).build());
			updateExpression.append(
					(updateExpression.isEmpty() ? "" : ", ") + TODO_UPDATED_AT_COL + " = :" + TODO_UPDATED_AT_COL);
		}
		if (!updateExpression.isEmpty()) {
			updateExpression.insert(0, "SET ");
		}

		LOG.debug("updateExpression : " + updateExpression);

		return UpdateItemRequest.builder() //
				.tableName(getTableName()) //
				.key(key) //
				.returnValues(ReturnValue.ALL_NEW) //
				.updateExpression(updateExpression.toString()) //
				.expressionAttributeValues(items).build();
	}

	protected DeleteItemRequest getDeleteItemRequest(String uuid) {

		Map<String, AttributeValue> key = new HashMap<>();
		key.put(TODO_UUID_COL, AttributeValue.builder().s(uuid).build());

		return DeleteItemRequest.builder() //
				.tableName(getTableName()) //
				.key(key) //
				.returnValues(ReturnValue.ALL_OLD)
				.build();
	}

}
