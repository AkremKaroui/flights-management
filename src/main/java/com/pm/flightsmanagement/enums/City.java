package com.pm.flightsmanagement.enums;

public enum City {

    TUNIS(Country.TUNISIA),
    PARIS(Country.FRANCE),
    LYON(Country.FRANCE),
    MILAN(Country.ITALY),
    ROMA(Country.ITALY),
    BRUSSELS(Country.BELGIUM),
    LIEGE(Country.BELGIUM),
    NY(Country.USA),
    LONDON(Country.UK),
    BERLIN(Country.GERMANY);

    City(Country country) {
    }
}
