package chapter_8.replace_type_code_with_subclasses;

public class SalesMan extends Employee {
    @Override
    int getType() {
        return Employee.SALESMAN;
    }
}
