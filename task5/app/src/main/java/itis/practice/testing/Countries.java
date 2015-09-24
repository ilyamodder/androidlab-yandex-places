package itis.practice.testing;

import java.util.List;
import java.util.Set;

/**
 * Created by ilya on 24.09.15.
 */
public class Countries {

    //errors
    boolean error;
    String message;

    public List<Country> countries;

    public static class Country {
        public String name;
        public String code;
        public List<City> cities;

        public Country(String name) {
            this.name = name;
        }


        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof Country)) return false;

            Country country = (Country) o;

            return name.equals(country.name);

        }

        @Override
        public int hashCode() {
            return name.hashCode();
        }
    }

    public static class City {
        String name;
        long population;
        Location location;
        List<District> districts;
        Set<Marker> markers;

        public City(String name) {
            this.name = name;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof City)) return false;

            City city = (City) o;

            return name.equals(city.name);

        }

        @Override
        public int hashCode() {
            return name.hashCode();
        }
    }

    public static class Location {
        public double latitude;
        public double longitude;
    }

    public static class District {
        String name;
        Size size;
    }

    public enum Size {
        SMALL, MEDIUM, LARGE
    }

    public enum Marker {
        COUNTRY_CAPITAL, STATE_CENTER, WITH_AIRPORT, BUSINESS_CENTER, RESORT
    }

}
