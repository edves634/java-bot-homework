package org.homework.di;

import org.homework.api.CommandService;
import org.homework.bot.Bot;
import org.homework.logger.LoggerImpl;
import org.homework.services.CommandServiceImpl;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;

import static org.junit.jupiter.api.Assertions.*;

class DIContainerTest {

    @Test
    void resolve_RegisteredInterface_ShouldReturnImplementation() {
        DIContainer container = new DIContainer();

        CommandService service = container.resolve(CommandService.class);
        assertNotNull(service);
        assertTrue(service instanceof CommandServiceImpl);
    }

    @Test
    void resolve_RegisteredClass_ShouldReturnInstance() {
        DIContainer container = new DIContainer();

        LoggerImpl logger = container.resolve(LoggerImpl.class);
        assertNotNull(logger);
    }

    @Test
    void resolve_WithDependencies_ShouldInjectThem() throws Exception {
        DIContainer container = new DIContainer();

        Bot bot = container.resolve(Bot.class);
        assertNotNull(bot);

        // Проверяем инъекцию через рефлексию
        Field commandServiceField = Bot.class.getDeclaredField("commandService");
        commandServiceField.setAccessible(true);
        assertNotNull(commandServiceField.get(bot));

        Field loggerField = Bot.class.getDeclaredField("logger");
        loggerField.setAccessible(true);
        assertNotNull(loggerField.get(bot));
    }

    @Test
    void resolve_UnregisteredType_ShouldThrowException() {
        DIContainer container = new DIContainer();

        assertThrows(IllegalStateException.class, () -> {
            container.resolve(Runnable.class); // Незарегистрированный тип
        });
    }
}