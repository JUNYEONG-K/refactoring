package chapter_8.replace_type_code_with_class.refactoring;

public class Main {
    public static void main(String[] args) {
        Person personA = new Person(BloodGroup.A);
        System.out.println("personA.getBloodGroup().getCode() = " + personA.getBloodGroup().getCode());
        int bloodGroupCode = personA.getBloodGroupCode();
        System.out.println("bloodGroupCode = " + bloodGroupCode);
        personA.setBloodGroup(BloodGroup.AB);
    }
}
