package chapter_7.inline_class.origin;

public class Person {
    private String _name;

    private final Telephone _officeTelephone = new Telephone();

    public String getName() {
        return _name;
    }
}
