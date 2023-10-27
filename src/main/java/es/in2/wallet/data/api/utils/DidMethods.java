package es.in2.wallet.data.api.utils;

public enum DidMethods {
    DID_KEY("did_key"),
    DID_ELSI("did_elsi");

    private final String stringValue;

    DidMethods(String stringValue) {
        this.stringValue = stringValue;
    }

    public String getStringValue() {
        return stringValue;
    }
}
