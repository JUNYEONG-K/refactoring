package chapter_7.inline_class.refactor;

public class Person {
    private String _name;

    private final Telephone _officeTelephone = new Telephone();

    public String getName() {
        return _name;
    }

    String getAreaCode() {
        return _officeTelephone.getOfficeAreaCode();
    }

    void setAreaCode(String areaCode) {
        _officeTelephone.setOfficeAreaCode(areaCode);
    }

    String getNumber() {
        return _officeTelephone.getOfficeNumber();
    }

    void setNumber(String number) {
        _officeTelephone.setOfficeNumber(number);
    }
}
