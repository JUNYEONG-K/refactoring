package chapter_8.uni_to_bidirectional;

import java.util.HashSet;
import java.util.Set;

public class Customer {
    private final Set orders = new HashSet();

    //헬퍼 메서드
    Set friendOrders() {
        return orders;
    }

    void addOrder(Order order) {
        order.setCustomer(this);
    }
}
