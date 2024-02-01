package chapter_8.replace_data_value_with_object.origin;

public class Order {
    private String _customer;

    public Order(String customer) {
        _customer = customer;
    }

    public String getCustomer() {
        return _customer;
    }

    public void setCustomer(String customer) {
        _customer = customer;
    }
}
