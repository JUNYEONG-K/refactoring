package chapter_7.move_method;

public class AccountTypeB extends AccountType {
    String type = "B";

    @Override
    double overdraftCharge(Account account) {
        System.out.println("isPremium() = " + isPremium());
        return account.getDaysOverdrawn() * 1.75;
    }

    @Override
    String getType() {
        return type;
    }
}
