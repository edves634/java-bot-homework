package org.homework.di.annotations;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.FIELD;

/**
 * Аннотация @Resolve используется для пометки полей,
 * которые должны быть автоматически разрешены (инъектированы)
 * контейнером зависимостей.
 */
@Retention(RetentionPolicy.RUNTIME) // Аннотация будет доступна в рантайме
@Target(FIELD) // Аннотация может применяться только к полям
public @interface Resolve {}
