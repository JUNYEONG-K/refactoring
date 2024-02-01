package chapter_7.hide_delegate.refactor;

public class Person {
    Department _department;

    public void setDepartment(Department department) {
        _department = department;
    }

    public Person getManager() {
        return _department.getManager();
    }
}
