package chapter_7.move_method;

public class AccountMain {
    public static void main(String[] args) {
        Account accountA = new Account(new AccountTypeA(), 9);
        Account accountB = new Account(new AccountTypeB(), 3);

        System.out.println(accountA.bankCharge());
        System.out.println(accountB.bankCharge());
    }
}
