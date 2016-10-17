package com.ddscanner.entities;

import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Root;
import org.simpleframework.xml.Text;

@Root(name="territory")
public class Country implements Comparable<Country> {

    @Attribute
    private String type;

    @Text
    private String countryName;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getCountryName() {
        return countryName;
    }

    public void setCountryName(String countryName) {
        this.countryName = countryName;
    }

    @Override
    public int compareTo(Country country) {
        return this.getCountryName().compareTo(country.getCountryName());
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof Country) {
            if (this.type.equalsIgnoreCase(((Country) o).getType())) {
                return true;
            } else {
                return false;
            }
        } else {
            return false;
        }
    }
}
