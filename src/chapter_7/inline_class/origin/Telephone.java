package chapter_7.inline_class.origin;

public class Telephone {
    private String _officeAreaCode;
    private String _officeNumber;
    public String getTelephoneNumber() {
        return ("(" + getOfficeAreaCode() + ") " + getOfficeNumber());
    }

    String getOfficeAreaCode() {
        return _officeAreaCode;
    }

    String getOfficeNumber() {
        return _officeNumber;
    }

    void setOfficeAreaCode(String _officeAreaCode) {
        this._officeAreaCode = _officeAreaCode;
    }

    void setOfficeNumber(String _officeNumber) {
        this._officeNumber = _officeNumber;
    }
}
