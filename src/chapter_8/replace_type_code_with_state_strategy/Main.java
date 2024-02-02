package chapter_8.replace_type_code_with_state_strategy;

public class Main {
    public static void main(String[] args) {
        Employee engineer = new Employee(EmployeeType.ENGINEER);
        Employee salesman = new Employee(EmployeeType.SALESMAN);
        Employee manger = new Employee(EmployeeType.MANAGER);
    }
}
