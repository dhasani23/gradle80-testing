package com.gradle80.api.aspect;

import com.gradle80.api.model.ApiRequest;
import com.gradle80.api.response.ErrorResponse;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Aspect for validating API requests before they are processed.
 * This aspect intercepts controller methods that receive ApiRequest objects
 * and validates them before allowing the method execution to continue.
 * 
 * If validation fails, it prevents the method execution and returns an error response.
 */
@Aspect
@Component
@Order(1) // High priority to ensure validation runs before other aspects
public class ApiValidationAspect {
    
    private static final Logger logger = LoggerFactory.getLogger(ApiValidationAspect.class);
    
    /**
     * Defines a pointcut for any controller method that has an ApiRequest parameter.
     * This matches methods in classes with @RestController or @Controller annotations.
     */
    @Pointcut("within(@org.springframework.web.bind.annotation.RestController *) || " +
              "within(@org.springframework.stereotype.Controller *)")
    public void controllerMethods() {}
    
    /**
     * Defines a pointcut for any method that receives an ApiRequest parameter.
     */
    @Pointcut("execution(* *(..)) && args(..)")
    public void methodsWithArgs() {}
    
    /**
     * Validates API requests before they are processed by controller methods.
     * If validation fails, logs the validation errors and throws an exception.
     *
     * @param joinPoint the join point representing the intercepted method
     * @throws Throwable if validation fails or if the method execution throws an exception
     */
    @Around("controllerMethods() && methodsWithArgs()")
    public Object validateRequest(ProceedingJoinPoint joinPoint) throws Throwable {
        Object[] args = joinPoint.getArgs();
        List<String> validationErrors = new ArrayList<>();
        
        // Check each argument to see if it's an ApiRequest
        for (Object arg : args) {
            if (arg instanceof ApiRequest) {
                ApiRequest request = (ApiRequest) arg;
                
                logger.debug("Validating request: {} with ID: {}", 
                    request.getClass().getSimpleName(), request.getRequestId());
                
                // Perform validation
                if (!request.validate()) {
                    String errorMsg = "Validation failed for " + 
                        request.getClass().getSimpleName() + 
                        " with ID: " + request.getRequestId();
                    
                    logger.warn(errorMsg);
                    validationErrors.add(errorMsg);
                    
                    // FIXME: Consider more specific validation method that returns detailed errors
                }
            }
        }
        
        // If there are validation errors, return an error response
        if (!validationErrors.isEmpty()) {
            ErrorResponse errorResponse = ErrorResponse.validationError(validationErrors);
            
            // Check if the return type is ResponseEntity to properly wrap the error
            if (joinPoint.getSignature().toString().contains("ResponseEntity")) {
                return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
            } else {
                // For non-ResponseEntity return types, we should consider how to properly handle this
                // TODO: Implement a global exception handler to standardize error responses
                return errorResponse;
            }
        }
        
        // If validation passes, proceed with the method execution
        return joinPoint.proceed();
    }
    
    /**
     * Logs diagnostic information about request validation.
     * This method runs before any controller method that has ApiRequest parameters.
     * 
     * @param joinPoint the join point representing the intercepted method
     */
    @Before("controllerMethods() && methodsWithArgs()")
    public void logRequestValidation(JoinPoint joinPoint) {
        if (logger.isDebugEnabled()) {
            String methodName = joinPoint.getSignature().getName();
            String className = joinPoint.getTarget().getClass().getSimpleName();
            
            List<String> apiRequestArgs = Arrays.stream(joinPoint.getArgs())
                .filter(arg -> arg instanceof ApiRequest)
                .map(arg -> ((ApiRequest) arg).getClass().getSimpleName() + "[" + 
                      ((ApiRequest) arg).getRequestId() + "]")
                .collect(Collectors.toList());
            
            if (!apiRequestArgs.isEmpty()) {
                logger.debug("API Validation for {}.{}() with requests: {}", 
                    className, methodName, String.join(", ", apiRequestArgs));
            }
        }
    }
    
    /**
     * TODO: Add support for field-level validation using Bean Validation API (JSR-380)
     * This would require adding validation constraints to request model fields.
     */
    
    /**
     * TODO: Add support for custom validation annotations to make validation more declarative
     * This would allow for more specific validation logic to be attached directly to controller methods.
     */
}