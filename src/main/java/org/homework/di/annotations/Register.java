package org.homework.di.annotations;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.TYPE;

/**
 * Аннотация @Register используется для регистрации классов,
 * интерфейсов или enum в контейнере зависимостей.
 *
 * При помощи этой аннотации можно пометить компоненты,
 * которые должны быть доступны для инъекции в других частях приложения.
 */
@Retention(RetentionPolicy.RUNTIME) // Аннотация доступна в runtime через рефлексию
@Target(TYPE) // Может применяться только к классам, интерфейсам и enum
public @interface Register {
    /**
     * Указывает имя, под которым данный компонент будет зарегистрирован.
     * Если имя не указано, будет использовано имя класса.
     *
     * @return имя компонента
     */
    String name() default ""; // Имя компонента для регистрации
}
