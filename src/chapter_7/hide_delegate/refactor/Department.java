package chapter_7.hide_delegate.refactor;

public class Department {
    private String _chargeCode;
    private final Person _manager;

    public Department(Person manager) {
        _manager = manager;
    }

    public Person getManager() {
        return _manager;
    }
}
