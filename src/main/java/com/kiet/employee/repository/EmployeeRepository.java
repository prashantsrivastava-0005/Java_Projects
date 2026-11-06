package com.kiet.employee.repository;

import com.kiet.employee.model.Employee;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public class EmployeeRepository {
    private final JdbcTemplate jdbc;

    public EmployeeRepository(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    private final RowMapper<Employee> mapper = (rs, rowNum) ->
        new Employee(rs.getInt("id"), rs.getString("name"),
                     rs.getString("department"), rs.getDouble("salary"));

    public int save(Employee e) {
        return jdbc.update("INSERT INTO employee(name, department, salary) VALUES (?, ?, ?)",
                e.getName(), e.getDepartment(), e.getSalary());
    }

    public List<Employee> findAll() {
        return jdbc.query("SELECT * FROM employee ORDER BY id", mapper);
    }

    public Optional<Employee> findById(int id) {
        List<Employee> result = jdbc.query("SELECT * FROM employee WHERE id = ?", mapper, id);
        return result.stream().findFirst();
    }

    public int update(int id, Employee e) {
        return jdbc.update("UPDATE employee SET name=?, department=?, salary=? WHERE id=?",
                e.getName(), e.getDepartment(), e.getSalary(), id);
    }

    public int delete(int id) {
        return jdbc.update("DELETE FROM employee WHERE id=?", id);
    }
}
