package chapter_8.replace_data_value_with_object.refactor;

public class Order {
    private final Customer _customer;

    public Order(String customerName) {
        _customer = new Customer(customerName);
    }

    public String getCustomerName() { return _customer.getName(); }

    public void setCustomerName(String customerName) {
        new Customer(customerName);
    }
}
