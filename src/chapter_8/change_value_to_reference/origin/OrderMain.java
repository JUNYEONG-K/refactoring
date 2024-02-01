package chapter_8.change_value_to_reference.origin;

import java.util.Collection;
import java.util.Iterator;

public class OrderMain {
    public static void main(String[] args) {

    }

    private static int numberOfOrdersFor(Collection orders, String customerName) {
        int result = 0;
        Iterator iter = orders.iterator();
        while (iter.hasNext()) {
            Order each = (Order) iter.next();
            if (each.getCustomerName().equals(customerName)) result++;
        }
        return result;
    }
}
