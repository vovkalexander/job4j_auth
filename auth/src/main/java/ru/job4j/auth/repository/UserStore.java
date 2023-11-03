package ru.job4j.auth.repository;

import org.springframework.stereotype.Component;
import ru.job4j.auth.domain.Person;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class UserStore {
    private final ConcurrentHashMap<String, Person> users = new ConcurrentHashMap<>();

    public void save(Person person) {
        users.put(person.getLogin(), person);
    }

    public Person findByUsername(String username) {
        return users.get(username);
    }

    public List<Person> findAll() {
        return new ArrayList<>(users.values());
    }
}
