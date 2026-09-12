package restful_booker.models;

import com.fasterxml.jackson.annotation.JsonProperty;

public record CreateBookingResponse(
        @JsonProperty("bookingid") int bookingId,
        Booking booking
) {
}
