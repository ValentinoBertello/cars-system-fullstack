package com.valentinobertello.carsys.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

/**
 * Anotación personalizada para validar contraseñas usando Bean Validation.
 * **/
@Documented
@Constraint(validatedBy = PasswordConstraintValidator.class)
@Retention(RetentionPolicy.RUNTIME)
@Target(value = {ElementType.FIELD, ElementType.ANNOTATION_TYPE})
public @interface ValidPassword {

    String message() default "Contraseña inválida";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
