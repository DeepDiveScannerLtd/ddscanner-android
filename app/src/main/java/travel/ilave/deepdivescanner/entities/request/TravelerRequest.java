package travel.ilave.deepdivescanner.entities.request;

/**
 * Created by Vitaly on 29.11.2015.
 */
public class TravelerRequest {
//    *googleId*, *bookingId*, *firstName*, *lastName*, *email*, *gsm*, *countryId*
    private String googleId;
    private String bookingId;
    private String firstName = "Petr";
    private String lastName = "Ivanov";
    private String email = "petrivanov@gmail.com";
    private String gsm = "gsm";
    private String countryId;

    public String getGoogleId() {
        return googleId;
    }

    public void setGoogleId(String googleId) {
        this.googleId = googleId;
    }

    public String getBookingId() {
        return bookingId;
    }

    public void setBookingId(String bookingId) {
        this.bookingId = bookingId;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getGsm() {
        return gsm;
    }

    public void setGsm(String gsm) {
        this.gsm = gsm;
    }

    public String getCountryId() {
        return countryId;
    }

    public void setCountryId(String countryId) {
        this.countryId = countryId;
    }
}
