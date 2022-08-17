package dk.jarry.aws.dynamodb.todo.control;

import javax.validation.Constraint;
import javax.validation.Payload;
import javax.validation.constraints.Pattern;
import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.ANNOTATION_TYPE;
import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.PARAMETER;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * Validation constraint for a UUID in string format.
 *
 * https://gist.github.com/mindhaq/ffc3378a271cb7e91b3819ff1abb675c
 *
 */
@Pattern(regexp = UUID.UUID_REGEXP)

@Retention(RUNTIME)
@Target({METHOD, FIELD, PARAMETER, ANNOTATION_TYPE})
@Constraint(validatedBy = {})
@Documented
public @interface UUID {

    public final static String MESSAGE = "UUID has wrong format";
    public final static String UUID_REGEXP = "^[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}$";
    public final static java.util.regex.Pattern UUID_REGEXP_PATTERN = java.util.regex.Pattern.compile(UUID_REGEXP);

    String message() default MESSAGE;

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

}