package chapter_6.remove_assignments_to_parameters;

public class PersonMain {
    public static void main(String[] args) {
        Person p1 = new Person("고준영", 25);
        System.out.println("p1 = " + p1);
        addAge(p1);
        System.out.println("p1 = " + p1);
        System.out.println("p1.age = " + p1.age);

        Person p2 = new Person("이수빈", 25);
        System.out.println("p2 = " + p2);
        addAgeReplace(p2);
        System.out.println("p2 = " + p2);
        System.out.println("p2.age = " + p2.age);
    }

    private static void addAge(Person person) {
        System.out.println("person = " + person);
        person.age += 1;
        System.out.println("person = " + person);
        System.out.println("person.age = " + person.age);
    }

    private static void addAgeReplace(Person person) {
        System.out.println("person = " + person);
        person = new Person(person.name, person.age + 1);
        System.out.println("person = " + person);
        System.out.println("person.age = " + person.age);
    }
}
