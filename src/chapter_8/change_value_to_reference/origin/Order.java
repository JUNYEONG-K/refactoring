package chapter_8.change_value_to_reference.origin;

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
