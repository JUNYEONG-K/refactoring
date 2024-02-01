package chapter_1.video_rental.refactor;

public class NewReleasePrice extends Price {
    @Override
    int getPriceCode() {
        return Movie.NEW_RELEASE;
    }

    @Override
    public double getCharge(int daysRented) {
        return daysRented * 3;
    }

    @Override // 재정의, NewReleasePrice에만 적용되는 내용이기 때문, 나머지 하위 클래스에서는 따로 구현x
    int getFrequentRenterPoints(int daysRented) {
        return daysRented > 1 ? 2 :1;
    }
}
