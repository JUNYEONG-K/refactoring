package chapter_8.replace_type_code_with_class.refactoring;

public class Person {
    public static final int O = BloodGroup.O.getCode();
    public static final int A = BloodGroup.A.getCode();
    public static final int B = BloodGroup.B.getCode();
    public static final int AB = BloodGroup.AB.getCode();

    private BloodGroup bloodGroup;

    public Person(BloodGroup bloodGroup) {
        this.bloodGroup = bloodGroup;
    }

    public int getBloodGroupCode() {
        return bloodGroup.getCode();
    }

    public BloodGroup getBloodGroup() { return  bloodGroup; }

    public void setBloodGroup(BloodGroup bloodGroup) {
        this.bloodGroup = bloodGroup;
    }
}
