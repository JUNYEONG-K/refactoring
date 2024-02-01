package chapter_8.change_value_to_reference.origin;

public class Customer {
    private final String _name;

    public Customer(String name) {
        _name = name;
    }

    public String getName() {
        return _name;
    }
}
