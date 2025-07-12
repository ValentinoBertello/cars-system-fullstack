package com.valentinobertello.carsys.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.passay.*;

import java.util.Arrays;

/**
 * Validador que implementa la lógica para la anotación @ValidPassword.
 * Usa la librería Passay para definir reglas de fortaleza de la contraseña.
 */
public class PasswordConstraintValidator implements ConstraintValidator<ValidPassword, String> {

    /**
     * Inicialización del validador. Se ejecuta al levantar la aplicación,
     * recibe la instancia de la anotación para leer parámetros si los tuviera.
     */
    @Override
    public void initialize(ValidPassword constraintAnnotation) {
        ConstraintValidator.super.initialize(constraintAnnotation);
    }

    /**
     * Método que comprueba si la contraseña es válida según las reglas.
     *
     * @param password la cadena a validar
     * @param context contexto de validación (personalizar mensajes)
     * @return true si cumple todeas las reglas, false en caso contrario
     * **/
    @Override
    public boolean isValid(String password, ConstraintValidatorContext context) {
        // Configura las reglas de Passay
        PasswordValidator validator = new PasswordValidator(Arrays.asList(
                new LengthRule(8, 35), // Longitud entre 8 y 30
                new CharacterRule(EnglishCharacterData.UpperCase, 1), // Al menos 1 mayúscula
                new CharacterRule(EnglishCharacterData.LowerCase, 1), // Al menos 1 minúscula
                new CharacterRule(EnglishCharacterData.Digit, 1), // Al menos 1 número
                new CharacterRule(EnglishCharacterData.Special, 1),

                // Regla para bloquear secuencias numéricas (ej: 123, 456)
                //new IllegalSequenceRule(EnglishSequenceData.Numerical, 3, false),

                // Regla para bloquear secuencias alfabéticas (ej: abc, def)
                //new IllegalSequenceRule(EnglishSequenceData.Alphabetical, 3, false),

                new WhitespaceRule() // No espacios en blanco
        ));

        RuleResult result = validator.validate(new PasswordData(password));
        if (result.isValid()) {
            return true; // La contraseña es válida
        }

        // Personaliza el mensaje de error
        context.disableDefaultConstraintViolation();
        context.buildConstraintViolationWithTemplate(
                String.join(", ", validator.getMessages(result))
        ).addConstraintViolation();

        return false;
    }
}
