package chapter_6.remove_assignments_to_parameters;

import java.util.Date;

public class Param {
    public static void main(String[] args) {
        Date d1 = new Date("1 Apr 98");
        System.out.println("d1 = " + d1);
        nextDateUpdate(d1);
        System.out.println("nextDate 메서드 실행 후 d1 값: " + d1);

        Date d2 = new Date("1 Apr 98");
        System.out.println("d2 = " + d2);
        nextDateUpdateReplace(d2);
        System.out.println("nextDate 메서드 실행 후 d2 값: " + d2);
    }

    private static void nextDateUpdate(Date arg) {
        System.out.println("arg = " + arg);
        arg.setDate(arg.getDate() + 1);
        System.out.println("nextDate 메서드 안의 arg 값: " + arg);
    }

    private static void nextDateUpdateReplace(Date arg) {
        System.out.println("arg = " + arg);
        arg = new Date(arg.getYear(), arg.getMonth(), arg.getDate() + 1);
        System.out.println("nextDate 메서드 안의 arg 값: " + arg);
    }
}
