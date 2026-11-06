package com.kiet.employee.controller;

import com.kiet.employee.model.Employee;
import com.kiet.employee.repository.EmployeeRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/employees")
public class EmployeeController {
    private final EmployeeRepository repository;

    public EmployeeController(EmployeeRepository repository) {
        this.repository = repository;
    }

    @PostMapping
    public ResponseEntity<?> addEmployee(@RequestBody Employee employee) {
        if (employee.getName() == null || employee.getName().isBlank()
                || employee.getDepartment() == null || employee.getDepartment().isBlank()
                || employee.getSalary() <= 0) {
            return ResponseEntity.badRequest().body(Map.of("message",
                    "Name, department and positive salary are required."));
        }
        repository.save(employee);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(Map.of("message", "Employee added successfully."));
    }

    @GetMapping
    public ResponseEntity<List<Employee>> getAllEmployees() {
        return ResponseEntity.ok(repository.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getEmployeeById(@PathVariable int id) {
        return repository.findById(id)
                .<ResponseEntity<?>>map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Map.of("message", "Employee not found.")));
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateEmployee(@PathVariable int id, @RequestBody Employee employee) {
        if (employee.getName() == null || employee.getName().isBlank()
                || employee.getDepartment() == null || employee.getDepartment().isBlank()
                || employee.getSalary() <= 0) {
            return ResponseEntity.badRequest().body(Map.of("message",
                    "Name, department and positive salary are required."));
        }
        if (repository.update(id, employee) == 0) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("message", "Employee not found."));
        }
        return ResponseEntity.ok(repository.findById(id).orElseThrow());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteEmployee(@PathVariable int id) {
        if (repository.delete(id) == 0) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("message", "Employee not found."));
        }
        return ResponseEntity.ok(Map.of("message", "Employee deleted successfully."));
    }
}
