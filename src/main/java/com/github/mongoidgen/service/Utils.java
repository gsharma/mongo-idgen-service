package com.github.mongoidgen.service;

import java.io.IOException;
import java.net.URL;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.regex.Pattern;

public final class Utils {
    public final static class StringUtils {
        public final static boolean isNullOrEmpty(String toCheck) {
            return toCheck == null || toCheck.trim().length() == 0;
        }

        public final static boolean validUrl(final String url, final boolean nonHttpOkay) {
            boolean valid = false;
            if (!isNullOrEmpty(url)) {
                String pattern = nonHttpOkay ? "^(https?|ftp|file)://[-a-zA-Z0-9+&@#/%?=~_|!:,.;]*[-a-zA-Z0-9+&@#/%=~_|]"
                        : "^(https?)://[-a-zA-Z0-9+&@#/%?=~_|!:,.;]*[-a-zA-Z0-9+&@#/%=~_|]";
                valid = Pattern.compile(pattern).matcher(url).matches();
            }
            return valid;
        }
    }

    public final static class PropertyUtils {
        public final static String readArtifactProperties() {
            StringBuilder builder = new StringBuilder("ion-idgenerator artifact properties:\n");
            try {
                Enumeration<URL> urls = Utils.class.getClassLoader().getResources("ion-idgenerator-scm.properties");
                while (urls.hasMoreElements()) {
                    Properties props = new Properties();
                    props.load(urls.nextElement().openStream());
                    builder.append(String.format("  %s:%s:%s\n    SHA:%s\n    %s@%s\n    %s\n",
                            props.getProperty("maven.groupId", "<NA>"), props.getProperty("maven.artifactId", "<NA>"),
                            props.getProperty("maven.version", "<NA>"), props.getProperty("gitRevision", "<NA>"),
                            props.getProperty("builtBy", "<NA>"), props.getProperty("builtOn", "<NA>"),
                            props.getProperty("builtAt", "<NA>")));
                }
            } catch (IOException exception) { // annoyance
            }
            return builder.toString();
        }
    }

    public final static class CollectionUtils {
        @SuppressWarnings("rawtypes")
        public final static String listToString(final List list) {
            StringBuilder listToString = new StringBuilder();
            if (list != null && !list.isEmpty()) {
                for (int iter = 0; iter < list.size(); iter++) {
                    Object entry = list.get(iter);
                    if (entry != null) {
                        listToString.append(entry.toString());
                        if (iter != list.size() - 1) {
                            listToString.append(",");
                        }
                    }
                }
            }
            return listToString.toString();
        }

        @SuppressWarnings("rawtypes")
        public final static String mapToString(final Map map) {
            StringBuilder mapString = new StringBuilder();
            if (map != null && !map.isEmpty()) {
                Map.Entry entry = null;
                for (Iterator iter = map.entrySet().iterator(); iter.hasNext();) {
                    entry = (Map.Entry) iter.next();
                    if (entry != null) {
                        mapString.append(entry.getKey()).append("=").append(entry.getValue()).append(",");
                    }
                }
            }
            return mapString.toString();
        }
    }
}