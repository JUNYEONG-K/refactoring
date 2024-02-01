package chapter_8.uni_to_bidirectional;

public class Order {
    private Customer customer;

    public Customer getCustomer() {
        return customer;
    }

    void setCustomer(Customer customer) {
        if (this.customer != null) this.customer.friendOrders().remove(this);
        this.customer = customer;
        if (this.customer != null) this.customer.friendOrders().add(this);
    }
}
