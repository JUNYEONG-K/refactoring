package chapter_7.move_method;

public abstract class AccountType {
    abstract double overdraftCharge(Account account);

    private String type;

    String getType() {
        return type;
    }
    protected boolean isPremium() {
        return getType().equals("A");
    }
}
