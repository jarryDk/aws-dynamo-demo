package dk.jarry.aws.dynamodb.todo.control;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Objects;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.validation.ConstraintViolationException;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.jboss.resteasy.reactive.server.ServerExceptionMapper;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

@ApplicationScoped
public class ServerExceptionMappers {

    @Inject
    ObjectMapper mapper;

    @ServerExceptionMapper(ConstraintViolationException.class)
    public Response handleConstraintViolationException(ConstraintViolationException ex) {
        ArrayNode arrayNode = mapper.createArrayNode();
        ex.getConstraintViolations() //
                .stream() //
                .filter(Objects::nonNull) //
                .forEach(cv -> { //
                    arrayNode.add(mapper.createObjectNode() //
                            // .put("stackTrace", stackTrace(ex)) //
                            .put("path", cv.getPropertyPath().toString()) //
                            .put("message", cv.getMessage()));
                });
        return Response.status(Status.BAD_REQUEST) //
                .entity(arrayNode.toString()) //
                .build();
    }

    @ServerExceptionMapper(WebApplicationException.class)
    public Response handleWebApplicationException(WebApplicationException ex) {
        int code = ex.getResponse().getStatus();
        String errorMessage = "{\"code\":500, \"message\":\"Unknown error\"}";
        ObjectNode error = mapper.createObjectNode() //
                // .put("stackTrace", stackTrace(ex)) //
                .put("code", code)
                .put("message", (ex.getMessage() != null ? ex.getMessage() : "")); //
        try {
            errorMessage = mapper.writeValueAsString(error);
        } catch (Exception e) {
        }

        return Response //
                .status(code) //
                .entity(errorMessage) //
                .build();
    }

    @ServerExceptionMapper(Exception.class)
    public Response handleException(Exception ex) {
        int code = Response.Status.INTERNAL_SERVER_ERROR.getStatusCode();
        String errorMessage = "{\"code\":500, \"message\":\"Unknown error\"}";
        ObjectNode error = mapper.createObjectNode() //
                // .put("stackTrace", stackTrace(ex)) //
                .put("code", code)
                .put("message", (ex.getMessage() != null ? ex.getMessage() : "")); //
        try {
            errorMessage = mapper.writeValueAsString(error);
        } catch (Exception e) {
        }

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
