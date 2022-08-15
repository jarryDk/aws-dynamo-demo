package dk.jarry.aws.dynamodb.todo.entity;

import java.time.ZonedDateTime;
import java.util.Map;
import java.util.UUID;

import org.eclipse.microprofile.openapi.annotations.media.Schema;

import dk.jarry.aws.dynamodb.todo.boundary.AbstractService;
import io.quarkus.runtime.annotations.RegisterForReflection;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;

@RegisterForReflection
public class ToDo {

	@Schema(readOnly = true)
	public String uuid;

	public String subject;
	public String body;

	@Schema(readOnly = true)
	public ZonedDateTime createdAt;
	@Schema(readOnly = true)
	public ZonedDateTime updatedAt;

	public ToDo() {
		this.uuid = UUID.randomUUID().toString();
		ZonedDateTime now = ZonedDateTime.now();
		this.createdAt = now;
		this.updatedAt = now;
	}

	public static ToDo from(Map<String, AttributeValue> item) {

		System.out.println(item.size());
		System.out.println(item.keySet());

		ToDo toDo = new ToDo();
		if (item != null && !item.isEmpty()) {
			toDo.setUuid(item.get(AbstractService.TODO_UUID_COL).s());
			toDo.setSubject(item.get(AbstractService.TODO_SUBJECT_COL).s());
			toDo.setBody(item.get(AbstractService.TODO_BODY_COL).s());
			if(item.containsKey(AbstractService.TODO_CREATED_AT_COL)){
				toDo.setCreatedAt(ZonedDateTime.parse(item.get(AbstractService.TODO_CREATED_AT_COL).s()));
			}
			if(item.containsKey(AbstractService.TODO_UPDATED_AT_COL)){
				toDo.setUpdatedAt(ZonedDateTime.parse(item.get(AbstractService.TODO_UPDATED_AT_COL).s()));
			}
		}
		return toDo;
	}

	public String getUuid() {
		return uuid;
	}

	public void setUuid(String uuid) {
		this.uuid = uuid;
	}

	public String getSubject() {
		return subject;
	}

	public void setSubject(String subject) {
		this.subject = subject;
	}

	public String getBody() {
		return body;
	}

	public void setBody(String body) {
		this.body = body;
	}

	public ZonedDateTime getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(ZonedDateTime createdAt) {
		this.createdAt = createdAt;
	}

	public ZonedDateTime getUpdatedAt() {
		return updatedAt;
	}

	public void setUpdatedAt(ZonedDateTime updatedAt) {
		this.updatedAt = updatedAt;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((body == null) ? 0 : body.hashCode());
		result = prime * result + ((createdAt == null) ? 0 : createdAt.hashCode());
		result = prime * result + ((subject == null) ? 0 : subject.hashCode());
		result = prime * result + ((updatedAt == null) ? 0 : updatedAt.hashCode());
		result = prime * result + ((uuid == null) ? 0 : uuid.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ToDo other = (ToDo) obj;
		if (body == null) {
			if (other.body != null)
				return false;
		} else if (!body.equals(other.body))
			return false;
		if (createdAt == null) {
			if (other.createdAt != null)
				return false;
		} else if (!createdAt.equals(other.createdAt))
			return false;
		if (subject == null) {
			if (other.subject != null)
				return false;
		} else if (!subject.equals(other.subject))
			return false;
		if (updatedAt == null) {
			if (other.updatedAt != null)
				return false;
		} else if (!updatedAt.equals(other.updatedAt))
			return false;
		if (uuid == null) {
			if (other.uuid != null)
				return false;
		} else if (!uuid.equals(other.uuid))
			return false;
		return true;
	}

	
}
