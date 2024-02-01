package chapter_1.video_rental.refactor;

import java.util.Enumeration;
import java.util.Vector;

public class Customer {
    private String name;
    private Vector rentals = new Vector();

    public Customer(String name) {
        this.name = name;
    }

    public void addRental(Rental arg) {
        rentals.addElement(arg);
    }

    public String getName() {
        return name;
    }

    public String statement() {
        Enumeration rentals = this.rentals.elements();
        String result = this.getName() + " 고객님의 대여 기록\n";
        while (rentals.hasMoreElements()) {
            Rental each = (Rental) rentals.nextElement();;

            // 이번에 대여하는 비디오 정보와 대여료를 출력
            result += "\t" + each.getMovie().getTitle() + "\t" +
                    String.valueOf(each.getCharge()) + "\n";
        }
        // 푸터 행 추가
        result += "누적 대여료: " + String.valueOf(getTotalCharge()) + "\n";
        result += "적립 포인트: " + String.valueOf(getTotalFrequentRenterPoints());
        return result;
    }

    public String htmlStatement() {
        Enumeration rentals = this.rentals.elements();
        String result = "<H1><EM>" + this.getName() + " 고객님의 대여 기록</EM></H1><P>\n";
        while (rentals.hasMoreElements()) {
            Rental each = (Rental) rentals.nextElement();

            result += each.getMovie().getTitle() + ": " +
                    String.valueOf(each.getCharge()) + "<BR>\n";
        }
        // 푸터 행 추가
        result += "<P>누적 대여료: <EM>" + String.valueOf(getTotalCharge()) + "</EM><P>\n";
        result += "적립 포인트: <EM>" +
                String.valueOf(getTotalFrequentRenterPoints()) + "</EM><P>";
        return result;
    }

    private int getTotalFrequentRenterPoints() {
        int result = 0;
        Enumeration rentals = this.rentals.elements();
        while (rentals.hasMoreElements()) {
            Rental each = (Rental) rentals.nextElement();
            result += each.getFrequentRenterPoints();
        }
        return result;
    }

    private double getTotalCharge() {
        double result = 0;
        Enumeration rentals = this.rentals.elements();
        while (rentals.hasMoreElements()) {
            Rental each = (Rental) rentals.nextElement();
            result += each.getCharge();
        }
        return result;
    }
}
