package chapter_1.video_rental.refactor;

public class Rental {
    private Movie movie;
    private int daysRented;

    public Rental(Movie movie, int daysRented) {
        this.movie = movie;
        this.daysRented = daysRented;
    }

    public Movie getMovie() {
        return movie;
    }

    public int getDaysRented() {
        return daysRented;
    }

    public double getCharge() {
        return movie.getCharge(this.daysRented);
    }

    public int getFrequentRenterPoints() {
        return movie.getFrequentRenterPoints(this.daysRented);
    }
}
