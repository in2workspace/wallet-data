package es.in2.walletdata.utils;

import lombok.Getter;

@Getter
public enum DidMethods {

    KEY("key"),
    ELSI("elsi");

    private final String stringValue;

    DidMethods(String stringValue) {
        this.stringValue = stringValue;
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

