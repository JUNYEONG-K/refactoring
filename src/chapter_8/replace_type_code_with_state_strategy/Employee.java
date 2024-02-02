package chapter_8.replace_type_code_with_state_strategy;

public class Employee {
    private EmployeeType type;
    private int monthlySalary;
    private int commission;
    private int bonus;

    void setType(int arg) {
        type = EmployeeType.newType(arg);
    }

    int getType() {
        return type.getTypeCode();
    };

    public Employee(int type) {
        setType(type);
    }

    int payAmount() {
        if (getType() == EmployeeType.ENGINEER) return monthlySalary;
        if (getType() == EmployeeType.SALESMAN) return monthlySalary + commission;
        if (getType() == EmployeeType.MANAGER) return monthlySalary + bonus;
        throw new IllegalArgumentException("분류 부호 값이 잘못됨.");
    }
}
