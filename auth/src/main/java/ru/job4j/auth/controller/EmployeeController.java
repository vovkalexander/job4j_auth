package ru.job4j.auth.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import ru.job4j.auth.domain.Employee;
import ru.job4j.auth.domain.Person;
import ru.job4j.auth.dto.PersonDTO;
import ru.job4j.auth.exception.Operation;
import ru.job4j.auth.repository.EmployeeRepository;

import javax.validation.Valid;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@RestController
@RequestMapping("/employee")
public class EmployeeController {

    private static final String API = "http://localhost:8080/person/";

    private static final String API_ID = "http://localhost:8080/person/{id}";

    @Autowired
    private RestTemplate rest;

    private final EmployeeRepository employeeRepository;

    public EmployeeController(final EmployeeRepository employeeRepository) {
        this.employeeRepository = employeeRepository;
    }

    @GetMapping("/")
    public Collection<Employee> findAll() {

        List<Person> persons = rest.exchange(
                API,
                HttpMethod.GET, null, new ParameterizedTypeReference<List<Person>>() { }
        ).getBody();

       Map<Integer, Employee> employeeMap = StreamSupport.stream(this.employeeRepository.findAll().spliterator(), false)
              .collect(Collectors.toMap(Employee::getId, Function.identity()));

        persons.stream().filter(person -> employeeMap.containsKey(person.getEmployeeId()))
              .forEach(person -> employeeMap.get(person.getEmployeeId()).getPerson().add(person));

        return  employeeMap.values();
    }

    @PatchMapping("/")
    public ResponseEntity<Person> updateLogin(@RequestBody @Valid PersonDTO persondto) {

        Person person = rest.patchForObject(API, persondto, Person.class);

        return new ResponseEntity<>(
                person,
                HttpStatus.OK
        );

    }

    @PostMapping("/")
    public ResponseEntity<Person> create(@RequestBody @Validated(Operation.OnCreate.class) Person person) {
        if (person.getLogin() == null || person.getPassword() == null) {

            throw new NullPointerException("Login and password mustn't be empty");
        }

        Person rsl = rest.postForObject(API + "sign-up", person, Person.class);
        return new ResponseEntity<>(
                rsl,
                HttpStatus.CREATED
        );
    }

    @PutMapping("/")
    public ResponseEntity<Void> update(@RequestBody @Validated(Operation.OnUpdate.class) Person person) {
        if (person.getLogin() == null || person.getPassword() == null) {

            throw new NullPointerException("Login and password mustn't be empty");
        }
        rest.put(API, person);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable int id) {
        rest.delete(API_ID, id);
        return ResponseEntity.ok().build();
    }

}
