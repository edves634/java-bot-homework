package org.homework.di;

import org.homework.di.annotations.Register;
import org.homework.di.annotations.Resolve;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Тестовый класс для {@link DIContainer}.
 * Проверяет функциональность контейнера внедрения зависимостей, включая:
 * - автоматическую регистрацию компонентов
 * - разрешение зависимостей
 * - внедрение зависимостей
 * - обработку ошибок
 */
class DIContainerTest {

    private DIContainer diContainer;

    // Тестовые интерфейсы и классы для проверки DI контейнера

    /** Тестовый сервисный интерфейс */
    public interface TestService {}

    /** Другой тестовый сервисный интерфейс */
    public interface AnotherService {}

    /** Реализация TestService с аннотацией @Register */
    @Register
    public static class TestServiceImpl implements TestService {
        public TestServiceImpl() {}
    }

    /** Реализация AnotherService с аннотацией @Register */
    @Register
    public static class AnotherServiceImpl implements AnotherService {
        public AnotherServiceImpl() {}
    }

    /** Сервис с зависимостями для тестирования внедрения */
    @Register
    public static class ServiceWithDependencies {
        @Resolve
        private TestService testService;

        @Resolve
        private AnotherService anotherService;

        public TestService getTestService() {
            return testService;
        }

        public AnotherService getAnotherService() {
            return anotherService;
        }
    }

    /** Конкретный сервис без интерфейса для тестирования */
    @Register
    public static class ConcreteService {
        public ConcreteService() {}
    }

    /** Незарегистрированная реализация для тестирования ошибок */
    public static class UnregisteredService implements TestService {}

    /**
     * Инициализация перед каждым тестом.
     * Создает новый экземпляр DIContainer.
     */
    @BeforeEach
    void setUp() {
        diContainer = new DIContainer();
    }

    /**
     * Тест проверяет, что autoRegister() корректно регистрирует аннотированные классы.
     */
    @Test
    void autoRegister_ShouldRegisterAnnotatedClasses() throws Exception {
        // Получаем доступ к приватному полю registeredImplementations через рефлексию
        Field implementationsField = DIContainer.class.getDeclaredField("registeredImplementations");
        implementationsField.setAccessible(true);
        @SuppressWarnings("unchecked")
        Map<Class<?>, Class<?>> implementations = (Map<Class<?>, Class<?>>) implementationsField.get(diContainer);

        // Проверяем регистрацию интерфейсов и их реализаций
        assertEquals(TestServiceImpl.class, implementations.get(TestService.class),
                "TestService должен быть зарегистрирован с реализацией TestServiceImpl");
        assertEquals(AnotherServiceImpl.class, implementations.get(AnotherService.class),
                "AnotherService должен быть зарегистрирован с реализацией AnotherServiceImpl");

        // Проверяем регистрацию конкретных классов
        assertEquals(ConcreteService.class, implementations.get(ConcreteService.class),
                "ConcreteService должен быть зарегистрирован сам в себя");
        assertEquals(ServiceWithDependencies.class, implementations.get(ServiceWithDependencies.class),
                "ServiceWithDependencies должен быть зарегистрирован сам в себя");
    }

    /**
     * Тест проверяет, что resolve() выбрасывает исключение для незарегистрированных классов.
     */
    @Test
    void resolve_ShouldThrowForUnregisteredClasses() {
        class UnregisteredClass {}

        assertThrows(IllegalStateException.class, () -> {
            diContainer.resolve(UnregisteredClass.class);
        }, "Должно быть выброшено исключение для незарегистрированного класса");
    }

    /**
     * Тест проверяет создание экземпляра конкретного класса.
     */
    @Test
    void resolve_ShouldCreateConcreteClassInstance() {
        ConcreteService instance = diContainer.resolve(ConcreteService.class);
        assertNotNull(instance, "Экземпляр ConcreteService должен быть создан");
    }

    /**
     * Тест проверяет корректное внедрение зависимостей.
     */
    @Test
    void createServiceFromClass_ShouldInjectDependencies() {
        ServiceWithDependencies instance = diContainer.resolve(ServiceWithDependencies.class);

        assertNotNull(instance, "Экземпляр ServiceWithDependencies должен быть создан");

        // Проверяем внедрение TestService
        assertNotNull(instance.getTestService(),
                "Зависимость TestService должна быть внедрена");
        assertTrue(instance.getTestService() instanceof TestServiceImpl,
                "Внедренная зависимость должна быть экземпляром TestServiceImpl");

        // Проверяем внедрение AnotherService
        assertNotNull(instance.getAnotherService(),
                "Зависимость AnotherService должна быть внедрена");
        assertTrue(instance.getAnotherService() instanceof AnotherServiceImpl,
                "Внедренная зависимость должна быть экземпляром AnotherServiceImpl");
    }

    /**
     * Тест проверяет внедрение в private поля.
     */
    @Test
    void injectDependencies_ShouldInjectPrivateFields() throws Exception {
        ServiceWithDependencies testObj = new ServiceWithDependencies();

        // Проверяем, что поля изначально null
        Field testServiceField = ServiceWithDependencies.class.getDeclaredField("testService");
        testServiceField.setAccessible(true);
        assertNull(testServiceField.get(testObj), "Поле testService должно быть null до внедрения");

        Field anotherServiceField = ServiceWithDependencies.class.getDeclaredField("anotherService");
        anotherServiceField.setAccessible(true);
        assertNull(anotherServiceField.get(testObj), "Поле anotherService должно быть null до внедрения");

        // Внедряем зависимости
        diContainer.injectDependencies(testObj);

        // Проверяем, что поля были внедрены
        assertNotNull(testServiceField.get(testObj),
                "Поле testService должно быть внедрено");
        assertNotNull(anotherServiceField.get(testObj),
                "Поле anotherService должно быть внедрено");
    }

    /**
     * Тест проверяет работу verifyNoArgConstructor для класса с конструктором по умолчанию.
     */
    @Test
    void verifyNoArgConstructor_ShouldReturnTrueForValidClass() {
        assertTrue(diContainer.verifyNoArgConstructor(TestServiceImpl.class),
                "Должен вернуть true для класса с конструктором по умолчанию");
    }

    /**
     * Тест проверяет работу verifyNoArgConstructor для класса без конструктора по умолчанию.
     */
    @Test
    void verifyNoArgConstructor_ShouldReturnFalseForInvalidClass() {
        class ClassWithParamConstructor {
            public ClassWithParamConstructor(String param) {}
        }
        assertFalse(diContainer.verifyNoArgConstructor(ClassWithParamConstructor.class),
                "Должен вернуть false для класса без конструктора по умолчанию");
    }

    /**
     * Тест проверяет обработку класса, реализующего несколько интерфейсов.
     */
    @Test
    void resolve_ShouldHandleClassWithMultipleInterfaces() {
        @Register
        class MultiInterfaceImpl implements TestService, AnotherService {}

        // Проверяем разрешение через оба интерфейса
        TestService asTestService = diContainer.resolve(TestService.class);
        AnotherService asAnotherService = diContainer.resolve(AnotherService.class);

        assertNotNull(asTestService, "Должен вернуть экземпляр через TestService");
        assertNotNull(asAnotherService, "Должен вернуть экземпляр через AnotherService");
    }

    /**
     * Тест проверяет обработку ошибки при создании экземпляра.
     */
    @Test
    void resolve_ShouldThrowWhenConstructorFails() {
        @Register
        class FailingService {
            public FailingService() {
                throw new RuntimeException("Constructor failed");
            }
        }

        assertThrows(RuntimeException.class, () -> {
            diContainer.resolve(FailingService.class);
        }, "Должен пробросить исключение из конструктора");
    }
}
