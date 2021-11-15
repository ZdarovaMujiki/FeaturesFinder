package com.ccfit.zdarovamujiki.featuresfinder.deserialized;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

public class GeoPointsList {
    @Getter @Setter
    private List<Address> hits;

    @Getter @Setter
    public static class Address {
        private String country;
        private String city;
        private String name;
        private Point point;

        @Getter @Setter
        public static class Point {
            private double lat;
            private double lng;
        }

        @Override
        public String toString() {
            StringBuilder stringBuilder = new StringBuilder();
            if (country != null) {
                stringBuilder.append(country).append(", ");
            }
            if (city != null) {
                stringBuilder.append(city).append(", ");
            }
            stringBuilder.append(name);
            return stringBuilder.toString();
        }
    }
}
