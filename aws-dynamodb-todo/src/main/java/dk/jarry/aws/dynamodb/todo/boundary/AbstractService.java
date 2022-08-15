package dk.jarry.aws.dynamodb.todo.boundary;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

import org.eclipse.microprofile.config.inject.ConfigProperty;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import dk.jarry.aws.dynamodb.todo.entity.ToDo;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;
import software.amazon.awssdk.services.dynamodb.model.DeleteItemRequest;
import software.amazon.awssdk.services.dynamodb.model.GetItemRequest;
import software.amazon.awssdk.services.dynamodb.model.PutItemRequest;
import software.amazon.awssdk.services.dynamodb.model.ReturnValue;
import software.amazon.awssdk.services.dynamodb.model.ScanRequest;
import software.amazon.awssdk.services.dynamodb.model.UpdateItemRequest;

public abstract class AbstractService {

	@Inject
	@ConfigProperty(defaultValue = "todos", name = "dynamoDbTableName")
	String dynamoDbTableName;

	@Inject
	ObjectMapper mapper;

	public final static String TODO_UUID_COL = "uuid";

	public final static String TODO_SUBJECT_COL = "subject";
	public final static String TODO_BODY_COL = "body";

	public final static String TODO_CREATED_AT_COL = "createdAt";
	public final static String TODO_UPDATED_AT_COL = "updatedAt";

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
						TODO_UPDATED_AT_COL
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
	 * @return
	 */
	protected GetItemRequest getGetItemRequest(String uuid) {

		Map<String, AttributeValue> key = new HashMap<>();
		key.put(TODO_UUID_COL, AttributeValue.builder().s(uuid).build());

		return GetItemRequest.builder() //
				.tableName(getTableName()) //				
				.key(key) //
				.build();
	}

	protected UpdateItemRequest getUpdateItemRequest(String uuid, ToDo toDo) {

		Map<String, AttributeValue> key = new HashMap<>();
		key.put(TODO_UUID_COL, AttributeValue.builder().s(uuid).build());

		Map<String, AttributeValue> items = new HashMap<>();
		items.put(":" + TODO_SUBJECT_COL, AttributeValue.builder().s(toDo.getSubject()).build());
		items.put(":" + TODO_BODY_COL, AttributeValue.builder().s(toDo.getBody()).build());
		items.put(":" + TODO_UPDATED_AT_COL, AttributeValue.builder().s(ZonedDateTime.now().format(DateTimeFormatter.ISO_DATE_TIME)).build());

		String updateExpression = "SET " + //
				TODO_SUBJECT_COL + " = :" + TODO_SUBJECT_COL + ", " + //
				TODO_BODY_COL + " = :" + TODO_BODY_COL + ", " + //
				TODO_UPDATED_AT_COL + " = :" + TODO_UPDATED_AT_COL ;

		return UpdateItemRequest.builder() //
				.tableName(getTableName()) //
				.key(key) //
				.returnValues(ReturnValue.ALL_NEW) //
				.updateExpression(updateExpression) //
				.expressionAttributeValues(items).build();
	}

	protected DeleteItemRequest getDeleteItemRequest(String uuid) {

		Map<String, AttributeValue> key = new HashMap<>();
		key.put(TODO_UUID_COL, AttributeValue.builder().s(uuid).build());

		return DeleteItemRequest.builder() //
				.tableName(getTableName()) //
				.key(key) //
				.build();
	}

	/*
	@Provider
	public class ErrorMapper implements ExceptionMapper<Exception> {

		@Override
		public Response toResponse(Exception exception) {
			int code = 500;
			if (exception instanceof WebApplicationException) {
				code = ((WebApplicationException) exception).getResponse().getStatus();
			}
			String errorMessage = null;
			ObjectNode error = mapper.createObjectNode() //
					.put("error", (exception.getMessage() != null ? exception.getMessage() : "")) //
					// .add("stackTrace", stackTrace(exception)) //
					.put("code", code);
			try{
				errorMessage = mapper.writeValueAsString(error);
			}catch(Exception e){}

			return Response //
				.status(code) //
				.entity(errorMessage) //
				.build();
		}

		String stackTrace(Exception exception) {
			StringWriter writer = new StringWriter();
			PrintWriter printWriter = new PrintWriter(writer);
			exception.printStackTrace(printWriter);
			return writer.toString();
		}

	}
 */

}
