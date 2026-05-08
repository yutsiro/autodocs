package ru.nsu.astakhov.autodocs.model;

import jakarta.persistence.Embeddable;

@Embeddable
public record Supervisor (
        String name, // фио
        String job, // место работы
        String position, // должность
        String degree, // учёная степень
        String title // учёное звание
) {
}