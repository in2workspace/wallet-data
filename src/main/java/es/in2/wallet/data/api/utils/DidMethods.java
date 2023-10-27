package es.in2.wallet.data.api.utils;

public enum DidMethods {
    DID_KEY("did:key"),
    DID_ELSI("did:elsi");

    private final String stringValue;

    DidMethods(String stringValue) {
        this.stringValue = stringValue;
    }

    public String getStringValue() {
        return stringValue;
    }

    public static DidMethods fromStringValue(String stringValue) {
        for (DidMethods method : DidMethods.values()) {
            if (method.getStringValue().equalsIgnoreCase(stringValue)) {
                return method;
            }
        }
        throw new IllegalArgumentException("Unknown DID method: " + stringValue);
    }
}

