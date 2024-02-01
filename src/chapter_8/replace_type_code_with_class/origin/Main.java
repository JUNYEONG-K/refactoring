package chapter_8.replace_type_code_with_class.origin;

public class Main {
    public static void main(String[] args) {
        Person personA = new Person(Person.A);
        System.out.println("personA.getBloodGroup() = " + personA.getBloodGroup());
        int bloodGroup = personA.getBloodGroup();
        System.out.println("bloodGroup = " + bloodGroup);
        personA.setBloodGroup(Person.AB);
        System.out.println("personA.getBloodGroup() = " + personA.getBloodGroup());
    }
}
