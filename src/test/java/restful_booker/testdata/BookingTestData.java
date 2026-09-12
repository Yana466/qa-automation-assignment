package restful_booker.testdata;

import restful_booker.models.AuthRequest;
import restful_booker.models.Booking;
import restful_booker.models.BookingDates;

public final class BookingTestData {
    private BookingTestData() {
    }

    public static AuthRequest adminCredentials() {
        return new AuthRequest("admin", "password123");
    }

    public static Booking originalBooking() {
        return new Booking(
                "Ada",
                "Lovelace",
                275,
                true,
                new BookingDates("2030-05-10", "2030-05-15"),
                "Breakfast"
        );
    }

    public static Booking updatedBooking() {
        return new Booking(
                "Ada",
                "Byron",
                325,
                false,
                new BookingDates("2030-05-11", "2030-05-17"),
                "Dinner"
        );
    }
}
