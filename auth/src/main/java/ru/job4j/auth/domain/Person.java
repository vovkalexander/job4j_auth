package ru.job4j.auth.domain;
import lombok.Data;
import ru.job4j.auth.exception.Operation;

import javax.persistence.*;

import javax.validation.constraints.*;
import java.util.Objects;

@Entity
@Data
public class Person {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Null(groups = Operation.OnCreate.class)
    @NotNull(message = "id must be not null", groups = Operation.OnUpdate.class)
    private Integer id;

    @NotBlank(message = "login must be not empty")
    private String login;

    @NotBlank(message = "password must be not empty", groups = {Operation.OnCreate.class,
            Operation.OnUpdate.class})
    @Size(min = 6, message = "password length must be not 6", groups = {Operation.OnCreate.class,
            Operation.OnUpdate.class})
    private String password;

    @Column(name = "employee_id")
    private Integer employeeId;

    public Person() {

    }

    public static Person of(String login, String password) {
        Person person = new Person();
        person.login = login;
        person.password = password;
        return person;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Person person = (Person) o;
        return id == person.id
                &&
                Objects.equals(login, person.login)
                &&
                Objects.equals(password, person.password);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, login, password);
    }
}
