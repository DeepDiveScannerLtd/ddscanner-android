package travel.ilave.deepdivescanner.entities.request;

/**
 * Created by Vitaly on 29.11.2015.
 */
public class BookingRequest {
//    *optionId*, *date*, *note*, *languageId*, *pickupId*, *priceId[]*, *count[]*
    private String optionId;
    private String date;
    private String note;
    private String languageId;
    private String pickupId;
    private String[] priceId;
    private String[] count;

    public String getOptionId() {
        return optionId;
    }

    public void setOptionId(String optionId) {
        this.optionId = optionId;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public String getLanguageId() {
        return languageId;
    }

    public void setLanguageId(String languageId) {
        this.languageId = languageId;
    }

    public String getPickupId() {
        return pickupId;
    }

    public void setPickupId(String pickupId) {
        this.pickupId = pickupId;
    }

    public String[] getPriceId() {
        return priceId;
    }

    public void setPriceId(String[] priceId) {
        this.priceId = priceId;
    }

    public String[] getCount() {
        return count;
    }

    public void setCount(String[] count) {
        this.count = count;
    }
}
