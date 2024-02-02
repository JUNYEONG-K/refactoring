package chapter_8.replace_type_code_with_subclasses;

public class Main {
    public static void main(String[] args) {
        Employee engineer = Employee.create(Employee.ENGINEER);
        SalesMan salesman = (SalesMan) Employee.create(Employee.SALESMAN);
        Employee manager = Employee.create(Employee.MANAGER);
    }
}
