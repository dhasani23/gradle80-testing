package com.gradle80.common.validation;

/**
 * Interface for validation implementations.
 * This interface defines a contract for classes that perform validation on objects.
 * Implementations should throw appropriate exceptions when validation fails.
 *
 * @param <T> the type of object to validate
 */
public interface Validator<T> {

    /**
     * Validates the provided object according to specific validation rules.
     * Implementation should throw appropriate exceptions when validation fails.
     *
     * @param object the object to validate
     * @throws IllegalArgumentException if the object fails validation due to invalid arguments
     * @throws NullPointerException if the object is null and null is not allowed
     * @throws ValidationException if validation fails for other reasons
     */
    void validate(T object);
}