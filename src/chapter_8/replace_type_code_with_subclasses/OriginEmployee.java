package chapter_8.replace_type_code_with_subclasses;

public class OriginEmployee {
    private int type;

    static final int ENGINEER = 0;
    static final int SALESMAN = 1;
    static final int MANAGER = 2;

    OriginEmployee(int type) {
        this.type = type;
    }
}
