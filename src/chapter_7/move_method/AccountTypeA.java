package chapter_7.move_method;

public class AccountTypeA extends AccountType {
    String type = "A";

    @Override
    public double overdraftCharge(Account account) {
        System.out.println("isPremium() = " + isPremium());
        if (isPremium()) {
            double result = 10;
            if (account.getDaysOverdrawn() > 7) result += (account.getDaysOverdrawn() - 7) * 0.85;
            return result;
        }
        return account.getDaysOverdrawn() * 1.75;
    }

    @Override
    String getType() {
        return type;
    }
}
