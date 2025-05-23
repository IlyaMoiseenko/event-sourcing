package org.example.eventsourcing.application.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.HashMap;
import java.util.Map;

/**
 * Глобальный обработчик исключений для REST API.
// */
//@ControllerAdvice
//public class GlobalExceptionHandler {
//
//    /**
//     * Обрабатывает исключения бизнес-логики.
//     *
//     * @param ex исключение
//     * @return ответ с ошибкой
//     */
//    @ExceptionHandler(IllegalStateException.class)
//    public ResponseEntity<Map<String, String>> handleIllegalStateException(IllegalStateException ex) {
//        Map<String, String> error = new HashMap<>();
//        error.put("error", ex.getMessage());
//        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
//    }
//
//    /**
//     * Обрабатывает ошибки валидации.
//     *
//     * @param ex исключение валидации
//     * @return ответ с ошибками валидации
//     */
//    @ExceptionHandler(MethodArgumentNotValidException.class)
//    public ResponseEntity<Map<String, String>> handleValidationException(MethodArgumentNotValidException ex) {
//        Map<String, String> errors = new HashMap<>();
//        ex.getBindingResult().getFieldErrors().forEach(error ->
//                errors.put(error.getField(), error.getDefaultMessage()));
//        return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);
//    }
//
//    /**
//     * Обрабатывает непредвиденные исключения.
//     *
//     * @param ex исключение
//     * @return ответ с общей ошибкой
//     */
//    @ExceptionHandler(Exception.class)
//    public ResponseEntity<Map<String, String>> handleGenericException(Exception ex) {
//        Map<String, String> error = new HashMap<>();
//        error.put("error", "Внутренняя ошибка сервера");
//        return new ResponseEntity<>(error, HttpStatus.INTERNAL_SERVER_ERROR);
//    }
//}
