package com.gradle80.data.converter;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Utility class for converting between entity and DTO objects.
 * Provides generic conversion methods to transform entities to DTOs and vice versa.
 * 
 * @author Gradle80 Team
 */
public class EntityConverter {
    
    private static final Logger LOGGER = Logger.getLogger(EntityConverter.class.getName());
    
    /**
     * Converts an entity object to a corresponding DTO object.
     * 
     * @param <E> Entity type
     * @param <T> DTO type
     * @param entity The entity object to convert
     * @param dtoClass The class of the target DTO
     * @return A new instance of the DTO populated with data from the entity
     */
    public static <E, T> T toDto(E entity, Class<T> dtoClass) {
        if (entity == null) {
            return null;
        }
        
        try {
            T dto = dtoClass.getDeclaredConstructor().newInstance();
            copyProperties(entity, dto);
            return dto;
        } catch (NoSuchMethodException e) {
            LOGGER.log(Level.SEVERE, "No default constructor found in DTO class: " + dtoClass.getName(), e);
            throw new IllegalArgumentException("DTO class must have a default constructor", e);
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error converting entity to DTO: " + e.getMessage(), e);
            throw new RuntimeException("Failed to convert entity to DTO", e);
        }
    }
    
    /**
     * Converts a DTO object to a corresponding entity object.
     * 
     * @param <T> DTO type
     * @param <E> Entity type
     * @param dto The DTO object to convert
     * @param entityClass The class of the target entity
     * @return A new instance of the entity populated with data from the DTO
     */
    public static <T, E> E toEntity(T dto, Class<E> entityClass) {
        if (dto == null) {
            return null;
        }
        
        try {
            E entity = entityClass.getDeclaredConstructor().newInstance();
            copyProperties(dto, entity);
            return entity;
        } catch (NoSuchMethodException e) {
            LOGGER.log(Level.SEVERE, "No default constructor found in entity class: " + entityClass.getName(), e);
            throw new IllegalArgumentException("Entity class must have a default constructor", e);
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error converting DTO to entity: " + e.getMessage(), e);
            throw new RuntimeException("Failed to convert DTO to entity", e);
        }
    }
    
    /**
     * Copies properties from source to target object using reflection.
     * Matches fields by name and attempts to copy values.
     * 
     * @param source The source object
     * @param target The target object
     */
    private static void copyProperties(Object source, Object target) {
        try {
            // Create field maps for both objects
            Map<String, Field> sourceFields = createFieldMap(source.getClass());
            Map<String, Field> targetFields = createFieldMap(target.getClass());
            
            // Copy matching fields
            for (Map.Entry<String, Field> entry : targetFields.entrySet()) {
                String fieldName = entry.getKey();
                Field targetField = entry.getValue();
                
                if (sourceFields.containsKey(fieldName)) {
                    Field sourceField = sourceFields.get(fieldName);
                    copyField(source, target, sourceField, targetField);
                }
            }
        } catch (Exception e) {
            LOGGER.log(Level.WARNING, "Error during property copying: " + e.getMessage(), e);
            // Continue with partial copy rather than failing completely
        }
    }
    
    /**
     * Creates a map of field names to Field objects for a given class.
     * Includes fields from superclasses.
     * 
     * @param clazz The class to analyze
     * @return A map of field names to Field objects
     */
    private static Map<String, Field> createFieldMap(Class<?> clazz) {
        Map<String, Field> fieldMap = new HashMap<>();
        
        // Get all fields including inherited ones
        Class<?> currentClass = clazz;
        while (currentClass != null && !currentClass.equals(Object.class)) {
            for (Field field : currentClass.getDeclaredFields()) {
                fieldMap.put(field.getName(), field);
            }
            currentClass = currentClass.getSuperclass();
        }
        
        return fieldMap;
    }
    
    /**
     * Copies value from source field to target field.
     * Tries to use setters/getters if available, falls back to direct field access.
     * 
     * @param source Source object
     * @param target Target object
     * @param sourceField Source field
     * @param targetField Target field
     */
    private static void copyField(Object source, Object target, Field sourceField, Field targetField) {
        try {
            // Check if types are compatible
            if (!isAssignable(sourceField.getType(), targetField.getType()) && 
                !isAssignable(targetField.getType(), sourceField.getType())) {
                // FIXME: Add support for type conversion between common types (like String to Integer)
                LOGGER.log(Level.FINE, "Skipping field due to incompatible types: " + sourceField.getName() + 
                           " (" + sourceField.getType() + " -> " + targetField.getType() + ")");
                return;
            }
            
            // Try to use getter method
            Object value = getValueFromGetterOrField(source, sourceField);
            
            // Try to use setter method
            if (!setValueWithSetterOrField(target, targetField, value)) {
                LOGGER.log(Level.FINE, "Could not set field: " + targetField.getName());
            }
        } catch (Exception e) {
            LOGGER.log(Level.FINE, "Error copying field " + sourceField.getName() + ": " + e.getMessage());
            // Continue with other fields
        }
    }
    
    /**
     * Gets value using getter method if available, otherwise directly accesses the field.
     * 
     * @param object The source object
     * @param field The field to read
     * @return The field value
     * @throws Exception If reading fails
     */
    private static Object getValueFromGetterOrField(Object object, Field field) throws Exception {
        String fieldName = field.getName();
        String getterName = "get" + Character.toUpperCase(fieldName.charAt(0)) + fieldName.substring(1);
        
        // For boolean fields, try isXxx pattern first
        if (field.getType() == boolean.class || field.getType() == Boolean.class) {
            String isGetterName = "is" + Character.toUpperCase(fieldName.charAt(0)) + fieldName.substring(1);
            try {
                Method isGetter = object.getClass().getMethod(isGetterName);
                return isGetter.invoke(object);
            } catch (NoSuchMethodException e) {
                // Try regular getter next
            }
        }
        
        try {
            Method getter = object.getClass().getMethod(getterName);
            return getter.invoke(object);
        } catch (NoSuchMethodException e) {
            // Fall back to direct field access
            boolean accessible = field.isAccessible();
            try {
                field.setAccessible(true);
                return field.get(object);
            } finally {
                field.setAccessible(accessible);
            }
        }
    }
    
    /**
     * Sets value using setter method if available, otherwise directly accesses the field.
     * 
     * @param object The target object
     * @param field The field to set
     * @param value The value to set
     * @return true if successful, false otherwise
     */
    private static boolean setValueWithSetterOrField(Object object, Field field, Object value) {
        if (value == null) {
            return true;  // Skip null values
        }
        
        String fieldName = field.getName();
        String setterName = "set" + Character.toUpperCase(fieldName.charAt(0)) + fieldName.substring(1);
        
        try {
            // Try to find setter method
            Method setter = object.getClass().getMethod(setterName, field.getType());
            setter.invoke(object, value);
            return true;
        } catch (NoSuchMethodException e) {
            // Fall back to direct field access
            try {
                boolean accessible = field.isAccessible();
                field.setAccessible(true);
                field.set(object, value);
                field.setAccessible(accessible);
                return true;
            } catch (Exception ex) {
                return false;
            }
        } catch (Exception e) {
            return false;
        }
    }
    
    /**
     * Checks if one type can be assigned to another.
     * Handles primitive types and their wrapper classes.
     * 
     * @param from Source type
     * @param to Target type
     * @return true if assignable, false otherwise
     */
    private static boolean isAssignable(Class<?> from, Class<?> to) {
        // Handle primitive types and wrappers
        if (from.isPrimitive() && !to.isPrimitive()) {
            from = getPrimitiveWrapper(from);
        } else if (!from.isPrimitive() && to.isPrimitive()) {
            to = getPrimitiveWrapper(to);
        }
        
        return to.isAssignableFrom(from);
    }
    
    /**
     * Gets the wrapper class for a primitive type.
     * 
     * @param primitive The primitive class
     * @return The corresponding wrapper class
     */
    private static Class<?> getPrimitiveWrapper(Class<?> primitive) {
        if (primitive == boolean.class) return Boolean.class;
        if (primitive == byte.class) return Byte.class;
        if (primitive == char.class) return Character.class;
        if (primitive == double.class) return Double.class;
        if (primitive == float.class) return Float.class;
        if (primitive == int.class) return Integer.class;
        if (primitive == long.class) return Long.class;
        if (primitive == short.class) return Short.class;
        if (primitive == void.class) return Void.class;
        return primitive;
    }
    
    // TODO: Add support for collection and map conversions
    
    // TODO: Add support for deep copying of nested objects
}