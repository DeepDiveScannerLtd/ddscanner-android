package com.ddscanner.entities;

public class OrderDetails extends Order {

    private String bookingId;
    private String pickupPoint;
    private String cancelation;

    public String getBookingId() {
        return bookingId;
    }

    public void setBookingId(String bookingId) {
        this.bookingId = bookingId;
    }

    public String getPickupPoint() {
        return pickupPoint;
    }

    public void setPickupPoint(String pickupPoint) {
        this.pickupPoint = pickupPoint;
    }

    public String getCancelation() {
        return cancelation;
    }

    public void setCancelation(String cancelation) {
        this.cancelation = cancelation;
    }
}
