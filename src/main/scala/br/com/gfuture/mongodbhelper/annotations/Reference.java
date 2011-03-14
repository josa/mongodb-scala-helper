package br.com.gfuture.mongodbhelper.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Define um relacionamento referencial
 * <p/>
 * exemplo:
 * <p/>
 * <code>
 *
 * @Reference var referente:AnyTipe = ...
 * </code>
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD})
public @interface Reference {

    AssociationType association() default AssociationType.ONE_TO_ONE;

    CascadeType cascade() default CascadeType.NONE;

}