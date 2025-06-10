package org.homework.di;

import org.homework.di.annotations.Register;
import org.homework.di.annotations.Resolve;
import org.reflections.Reflections;
import org.reflections.scanners.SubTypesScanner;
import org.reflections.scanners.TypeAnnotationsScanner;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Контейнер внедрения зависимостей (DI), который автоматически регистрирует и управляет
 * жизненным циклом сервисов.
 * Поддерживает автоматическое обнаружение компонентов с аннотацией @Register
 * и внедрение зависимостей через аннотацию @Resolve.
 */
public class DIContainer {
    // Кэш созданных экземпляров сервисов (синглтоны)
    private final Map<Class<?>, Object> createdServices = new HashMap<>();

    // Регистр соответствий интерфейсов/классов их реализациям
    private final Map<Class<?>, Class<?>> registeredImplementations = new HashMap<>();

    /**
     * Конструктор контейнера. При создании автоматически сканирует пакет org.homework
     * и регистрирует все классы, помеченные аннотацией @Register.
     */
    public DIContainer() {
        autoRegister();
    }

    /**
     * Автоматическое сканирование и регистрация компонентов.
     * Находит все классы с аннотацией @Register в пакете org.homework и его подпакетах.
     */
    private void autoRegister() {
        // Используем Reflections для сканирования классов
        Reflections reflections = new Reflections("org.homework",
                new SubTypesScanner(false),
                new TypeAnnotationsScanner());

        // Получаем все классы с аннотацией @Register
        Set<Class<?>> annotated = reflections.getTypesAnnotatedWith(Register.class);

        for (Class<?> clazz : annotated) {
            if (clazz.isInterface()) {
                continue; // Пропускаем интерфейсы, так как ищем только реализации
            }

            if (!verifyNoArgConstructor(clazz)) {
                continue; // Пропускаем классы без конструктора по умолчанию
            }

            Class<?>[] interfaces = clazz.getInterfaces();
            for (Class<?> intf : interfaces) {
                // Регистрируем реализацию интерфейса, если:
                // 1. Реализация еще не зарегистрирована ИЛИ
                // 2. Текущая зарегистрированная реализация - это интерфейс (заменяем на конкретный класс)
                if (!registeredImplementations.containsKey(intf) || registeredImplementations.get(intf).isInterface()) {
                    registeredImplementations.put(intf, clazz);
                }
            }

            // Если у класса нет интерфейсов, регистрируем его как сам в себя
            if (interfaces.length == 0) {
                registeredImplementations.put(clazz, clazz);
            }
        }
    }

    /**
     * Получает экземпляр сервиса указанного типа.
     * @param serviceClass класс или интерфейс сервиса
     * @param <T> тип сервиса
     * @return экземпляр сервиса
     * @throws IllegalStateException если для запрошенного сервиса нет зарегистрированной реализации
     */
    public <T> T resolve(Class<T> serviceClass) {
        // Проверяем, зарегистрирован ли запрашиваемый сервис
        if (!registeredImplementations.containsKey(serviceClass)) {
            throw new IllegalStateException("No implementation registered for: " + serviceClass.getName());
        }

        // Пытаемся получить существующий экземпляр из кэша
        @SuppressWarnings("unchecked")
        T service = (T) createdServices.get(serviceClass);
        if (service == null) {
            service = createService(serviceClass);
        }
        return service;
    }

    /**
     * Создает экземпляр сервиса указанного типа.
     * @param serviceClass класс или интерфейс сервиса
     * @param <T> тип сервиса
     * @return новый экземпляр сервиса
     */
    private <T> T createService(Class<T> serviceClass) {
        // Разделяем логику создания для интерфейсов и конкретных классов
        return serviceClass.isInterface()
                ? createServiceFromInterface(serviceClass)
                : createServiceFromClass(serviceClass);
    }

    /**
     * Создает экземпляр сервиса для интерфейса, используя зарегистрированную реализацию.
     * @param serviceClass интерфейс сервиса
     * @param <T> тип интерфейса
     * @return экземпляр реализации
     * @throws IllegalStateException если для интерфейса не найдена реализация
     */
    private <T> T createServiceFromInterface(Class<T> serviceClass) {
        Class<?> implementationClass = registeredImplementations.get(serviceClass);
        if (implementationClass == null) {
            throw new IllegalStateException("No implementation registered for interface: " + serviceClass.getName());
        }
        return createServiceFromClass(implementationClass.asSubclass(serviceClass));
    }

    /**
     * Создает экземпляр конкретного класса сервиса и внедряет его зависимости.
     * @param concreteClass конкретный класс сервиса
     * @param <T> тип сервиса
     * @return новый экземпляр сервиса
     * @throws RuntimeException если не удается создать экземпляр
     */
    private <T> T createServiceFromClass(Class<T> concreteClass) {
        try {
            // Создаем экземпляр через конструктор по умолчанию
            T instance = concreteClass.getDeclaredConstructor().newInstance();

            // Сохраняем в кэш перед инъекцией зависимостей, чтобы избежать циклических зависимостей
            createdServices.put(concreteClass, instance);

            // Внедряем зависимости
            injectDependencies(instance);

            return instance;
        } catch (Exception e) {
            throw new RuntimeException("Could not instantiate service: " + concreteClass.getName(), e);
        }
    }

    /**
     * Внедряет зависимости в поля объекта, помеченные аннотацией @Resolve.
     * @param object объект для внедрения зависимостей
     * @throws RuntimeException если не удается внедрить зависимость
     */
    void injectDependencies(Object object) {
        Class<?> clazz = object.getClass();

        // Перебираем все поля класса
        for (Field field : clazz.getDeclaredFields()) {
            if (field.isAnnotationPresent(Resolve.class)) {
                // Делаем поле доступным (даже если оно private)
                field.setAccessible(true);

                // Получаем тип зависимости
                Class<?> dependencyType = field.getType();

                // Получаем экземпляр зависимости
                Object dependency = resolve(dependencyType);

                try {
                    // Внедряем зависимость
                    field.set(object, dependency);
                } catch (IllegalAccessException e) {
                    throw new RuntimeException(
                            String.format("Could not set field '%s' on class '%s'.",
                                    field.getName(), clazz.getName()), e);
                }
            }
        }
    }

    /**
     * Проверяет наличие конструктора без аргументов у класса.
     * @param clazz класс для проверки
     * @return true, если класс имеет конструктор по умолчанию, иначе false
     */
    boolean verifyNoArgConstructor(Class<?> clazz) {
        try {
            clazz.getDeclaredConstructor();
            return true;
        } catch (NoSuchMethodException e) {
            // Класс не имеет конструктора по умолчанию - не может быть использован в DI
            return false;
        }
    }
}