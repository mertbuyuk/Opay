package com.opay.service;

public record ReservationOutcome(Type type, String insufficientSku) {

    public enum Type {
        RESERVED,
        OUT_OF_STOCK,
        /** The same order id was already processed (idempotent redelivery) -- do nothing further. */
        ALREADY_PROCESSED
    }

    public static ReservationOutcome reserved() { return new ReservationOutcome(Type.RESERVED,null);}
    public static ReservationOutcome outOfStock(String sku) {return new ReservationOutcome(Type.OUT_OF_STOCK,sku);}
    public static ReservationOutcome alreadProcessed(){ return new ReservationOutcome(Type.ALREADY_PROCESSED, null);}
}
