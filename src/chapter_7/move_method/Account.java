package chapter_7.move_method;

public class Account {
    private final AccountType _type;
    private final int _daysOverdrawn;

    public Account(AccountType _type, int _daysOverdrawn) {
        this._type = _type;
        this._daysOverdrawn = _daysOverdrawn;
    }

    double bankCharge() {
        double result = 4.5;
        if (_daysOverdrawn > 0) result += _type.overdraftCharge(this);
        return result;
    }

    public int getDaysOverdrawn() {
        return _daysOverdrawn;
    }
}
