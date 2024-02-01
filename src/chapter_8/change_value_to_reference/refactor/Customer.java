package chapter_8.change_value_to_reference.refactor;

import java.util.Dictionary;
import java.util.Hashtable;

public class Customer {
    private static final Dictionary _instances = new Hashtable();
    private final String _name;

    public static Customer getNamed(String customerName) {
        return (Customer) _instances.get(customerName);
    }

    static void loadCustomers() {
        new Customer("우리 렌터카").store();
        new Customer("커피 자판기 운영업 협동조합").store();
        new Customer("삼천리 가스 공장").store();
    }

    private void store() {
        _instances.put(this.getName(), this);
    }
    private Customer(String name) {
        _name = name;
    }

    public String getName() {
        return _name;
    }
}
