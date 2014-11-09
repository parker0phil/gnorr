package com.parker0phil.gnorr


class NamedIdentifier {

    public static String PATH_MATCHER = '@[a-zA-Z]{1}[a-zA-Z0-9_-]*=.*'

    private String identifierName, identifierValue;

    private NamedIdentifier(String identifierName, String identifierValue) {
        this.identifierName = identifierName
        this.identifierValue = identifierValue
    }

    String getIdentifierName() {
        return identifierName
    }

    String getIdentifierValue() {
        return identifierValue
    }

    public static NamedIdentifier from(String pathIdentifier){
        def identifier = pathIdentifier - '@'
        def parts = identifier.split('=')
        new NamedIdentifier(parts[0], parts[1..-1].join('='))
    }
}
