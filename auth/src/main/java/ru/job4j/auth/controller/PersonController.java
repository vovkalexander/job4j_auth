package ru.job4j.auth.controller;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.util.MultiValueMapAdapter;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import ru.job4j.auth.domain.Person;
import ru.job4j.auth.repository.PersonRepository;
import org.springframework.http.HttpStatus;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@RestController
@RequestMapping("/person")
@AllArgsConstructor
@Slf4j
public class PersonController {
    private final PersonRepository persons;
    private final BCryptPasswordEncoder encoder;
    private final ObjectMapper objectMapper;

    @GetMapping("/")
    public ResponseEntity<List<Person>> findAll() {

        return  ResponseEntity
                .status(HttpStatus.OK)
                .contentType(MediaType.APPLICATION_JSON)
                .body(StreamSupport.stream(
                        this.persons.findAll().spliterator(), false
                ).collect(Collectors.toList()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Person> findById(@PathVariable  int id) {

        return  new ResponseEntity<>(persons.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Person is not found. Please, check id")),
                new MultiValueMapAdapter<>(Map.of("Job4jCustomHeader", List.of("findById"))),
                HttpStatus.OK);
    }

    @PostMapping("/sign-up")
    public ResponseEntity<Person> create(@RequestBody Person person) {
        validate(person);
        person.setPassword(encoder.encode(person.getPassword()));

        return  ResponseEntity
                .status(HttpStatus.CREATED)
                .contentType(MediaType.APPLICATION_JSON)
                .body(persons.save(person));
    }

    @PutMapping("/update")
    public ResponseEntity<Person> update(@RequestBody  Person person) {
        validate(person);
        person.setPassword(encoder.encode(person.getPassword()));

        return new ResponseEntity(
                persons.save(person),
                new MultiValueMapAdapter<>(Map.of("Job4jCustomHeader", List.of("update"))),
                HttpStatus.OK
        );

    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable int id) {
        Person person = new Person();
        person.setId(id);
        this.persons.delete(person);

        return ResponseEntity
                .status(HttpStatus.NO_CONTENT)
                .contentType(MediaType.APPLICATION_JSON)
                .header("Job4jCustomHeader", "delete")
                .build();
    }

    private void validate(Person person) {
        if (person.getLogin() == null || person.getPassword() == null) {

            throw new NullPointerException("Login and password mustn't be empty");
        }
        if (person.getPassword().length() < 6 || person.getLogin().contains("@")) {

            throw new IllegalArgumentException("The password's length is less six or invalid symbols");
        }
    }

    @ExceptionHandler(value = {IllegalArgumentException.class})
    public void exceptionHandler(Exception e, HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        response.setStatus(HttpStatus.BAD_REQUEST.value());
        response.setContentType("application/json");
        response.getWriter().write(objectMapper.writeValueAsString(new HashMap<>() { {
            put("message", e.getMessage());
            put("type", e.getClass());
        }}));
        log.error(e.getLocalizedMessage());
    }

}
