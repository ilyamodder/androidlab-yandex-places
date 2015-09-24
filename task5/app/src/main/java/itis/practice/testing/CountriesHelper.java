package itis.practice.testing;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;
import static itis.practice.testing.Countries.Marker.*;

public class CountriesHelper {

    public static Countries deserialize(String json) throws ServerException {
        Countries countries =  new Gson().fromJson(json, Countries.class);
        if (countries.error) {
            throw new ServerException(countries.message);
        }
        if (countries.countries == null) {
            throw new IllegalArgumentException("json can't be empty");
        }
        return countries;
    }

    public static List<Countries.Country> findAllCountriesWithMoreThan27Cities(Countries countries){
        List<Countries.Country> resultCountries = new ArrayList<>();

        for (Countries.Country country : countries.countries) {
            if (country.cities.size() > 27) {
                resultCountries.add(country);
            }
        }

        return resultCountries;
    }

    public static List<Countries.City>
            findAllCitiesWithPopulationMoreThan45Million(Countries countries) {
        List<Countries.City> resultCities = new ArrayList<>();

        for (Countries.Country country : countries.countries) {
            for (Countries.City city : country.cities) {
                if (city.population > 45_000_000) {
                    resultCities.add(city);
                }
            }
        }

        return resultCities;
    }

    public static Countries.Country findCountryWithLargestPopulation(Countries countries) {

        long resultPopulation = -1;
        Countries.Country resultCountry = null;

        for (Countries.Country country : countries.countries) {

            long population = 0;
            for (Countries.City city : country.cities) {
                population += city.population;
            }
            if (population > resultPopulation) {
                resultPopulation = population;
                resultCountry = country;
            }
        }

        return resultCountry;
    }

    public static Countries.Country
            findCountryWithLargestAmountOfResortsCapitalsAirports(Countries countries) {
        Countries.Country resultCountry = null;
        int resultCitiesCount = -1;

        for (Countries.Country country : countries.countries) {
            int citiesCount = 0;

            for (Countries.City city : country.cities) {
                if (city.markers.contains(RESORT) && city.markers.contains(COUNTRY_CAPITAL)
                        && city.markers.contains(WITH_AIRPORT)) {
                    citiesCount++;
                }
            }

            if (citiesCount > resultCitiesCount) {
                resultCitiesCount = citiesCount;
                resultCountry = country;
            }
        }

        return resultCountry;
    }

    public static Countries.Country
            findFirstCountryWithAllCitiesLatitudeMoreThan60(Countries countries) {
        for (Countries.Country country : countries.countries) {
            int citiesCount = 0;
            for (Countries.City city : country.cities) {
                if (city.location.latitude > 60) citiesCount++;
            }
            if (citiesCount == country.cities.size()) return country;
        }

        return null;
    }

    public static boolean hasCountryWithTwoCitiesWithAtLeast7BigDistricts(Countries countries) {
        for (Countries.Country country : countries.countries) {
            int citiesCount = 0;
            for (Countries.City city : country.cities) {
                int bigDistrictsCount = 0;
                for (Countries.District district : city.districts) {
                    if (district.size == Countries.Size.LARGE) bigDistrictsCount++;
                }
                if (bigDistrictsCount >= 7) citiesCount++;
            }
            if (citiesCount >= 2) return true;
        }
        return false;
    }
}
