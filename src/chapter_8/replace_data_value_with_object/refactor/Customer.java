package chapter_8.replace_data_value_with_object.refactor;

public class Customer {
    private final String _name;

    public Customer(String name) {
        _name = name;
    }

    public String getName() {
        return _name;
    }
}
