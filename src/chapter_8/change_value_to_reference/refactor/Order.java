package chapter_8.change_value_to_reference.refactor;

public class Order {
    private final Customer _customer;

    public Order(String customerName) {
        _customer = Customer.getNamed(customerName);
    }

    public String getCustomerName() { return _customer.getName(); }
}
