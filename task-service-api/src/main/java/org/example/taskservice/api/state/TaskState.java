package org.example.taskservice.api.state;

public enum TaskState {
    CREATED("Задача создана"),
    WAITING_SA_RESOURSE("Ожидает назначения системному аналитику"),
    SA("Производится работа системного аналитика"),
    WAITING_DEV_RESOURSE("Ожидает назначения разработчика"),
    DEV("Разработка"),
    WAITING_TEST_RESOURSE("Ожидает тестера"),
    TEST("Тестируется"),
    DONE("Выполнена");

    private final String description;

    TaskState(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    public boolean isFinal() {
        return this == DONE;
    }

    public boolean requiresAssignment() {
        return this == WAITING_SA_RESOURSE
                || this == WAITING_DEV_RESOURSE
                || this == WAITING_TEST_RESOURSE;
    }
}