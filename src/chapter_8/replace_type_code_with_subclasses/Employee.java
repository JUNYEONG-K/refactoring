package chapter_8.replace_type_code_with_subclasses;

public abstract class Employee {
    private int type;

    static final int ENGINEER = 0;
    static final int SALESMAN = 1;
    static final int MANAGER = 2;

    static Employee create(int type) {
        if (type == ENGINEER) return new Engineer();
        if (type == SALESMAN) return new SalesMan();
        if (type == MANAGER) return new Manager();
        throw new IllegalArgumentException("분류 부호 값이 잘못됨.");
    }

    Employee() {
        this.type = this.getType();
    }

    abstract int getType();
}
