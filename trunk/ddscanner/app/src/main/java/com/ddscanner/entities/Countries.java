package com.ddscanner.entities;

import android.text.TextUtils;

import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Root;

import java.util.List;

@Root(name="territories")
public class Countries {

    @ElementList(inline=true)
    private List<Country> countries;

    public List<Country> getCountries() {
        return countries;
    }

    public void setCountries(List<Country> countries) {
        this.countries = countries;
    }

    public Country getCountry(String type) {
        if (TextUtils.isEmpty(type)) {
            return null;
        }
        for (Country country : countries) {
            if (country.getType().equalsIgnoreCase(type)) {
                return country;
            }
        }
        return null;
    }
}
