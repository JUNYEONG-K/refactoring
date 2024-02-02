package chapter_8.replace_type_code_with_state_strategy;

public abstract class EmployeeType {
    abstract int getTypeCode();

    static final int ENGINEER = 0;
    static final int SALESMAN = 1;
    static final int MANAGER = 2;

    static EmployeeType newType(int code) {
        if (code == EmployeeType.ENGINEER) return new Engineer();
        if (code == EmployeeType.SALESMAN) return new Salesman();
        if (code == EmployeeType.MANAGER) return new Manager();
        throw new IllegalArgumentException("분류 부호가 잘못됨.");
    }
}
