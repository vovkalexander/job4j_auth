package ru.job4j.auth.repository;

import org.springframework.data.repository.CrudRepository;
import ru.job4j.auth.domain.Employee;

import java.util.List;
import java.util.Map;
public interface EmployeeRepository extends CrudRepository<Employee, Integer> {

}
