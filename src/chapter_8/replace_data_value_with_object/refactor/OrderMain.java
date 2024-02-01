package chapter_8.replace_data_value_with_object.refactor;

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
