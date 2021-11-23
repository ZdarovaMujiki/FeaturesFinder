package com.ccfit.zdarovamujiki.featuresfinder.deserialized;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter @Setter
public class FeatureList {
    private List<Feature> features;

    @Getter @Setter
    public static class Feature {
        private Properties properties;

        @Getter @Setter
        public static class Properties {
            private String xid;
            private String name;
        }
    }
}
