package ru.practicum.shareit.user.model;

import lombok.Data;

import java.util.Objects;
import java.util.Set;

@Data
public class User {
    private Long id;
    private String name;
    private String email;
    private Set<Long> items;

    @Override
    public boolean equals(Object object) {
        if (this == object) return true;
        if (object == null || getClass() != object.getClass()) return false;
        User user = (User) object;
        return Objects.equals(email, user.email);
    }

    @Override
    public int hashCode() {
        return Objects.hash(email);
    }
}

