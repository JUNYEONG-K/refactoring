package chapter_7.extract_class;

public class OriginPerson {
    private String _name;
    private String _officeAreaCode;
    private String _officeNumber;

    public String getName() {
        return _name;
    }

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
