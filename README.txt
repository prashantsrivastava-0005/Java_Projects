Q2 Employee Management REST API (Spring Boot + JdbcTemplate + MySQL)
1. In Workbench run: CREATE DATABASE employee_db;
2. Open this folder in IntelliJ as a Maven project and wait for dependencies to download.
3. In src/main/resources/application.properties replace YOUR_MYSQL_PASSWORD.
4. Run EmployeeManagementApplication.java.
5. Test endpoints in Postman (base URL http://localhost:8080/api/employees):
   POST /api/employees
   GET /api/employees
   GET /api/employees/1
   PUT /api/employees/1
   DELETE /api/employees/1
The employee table is created automatically from schema.sql.
Use JSON body examples from the submission guide. Never commit your real password to GitHub.
