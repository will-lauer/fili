// Copyright 2020 Oath Inc.
// Licensed under the terms of the Apache license. Please see LICENSE.md file distributed with this work for terms.
package com.yahoo.bard.webservice.data.metric.protocol.utils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MetricDetailParser {

    private final String nameFromParentheticalPattern = "([\\w\\s]+)(.*)";
    private final String nameValuePairPattern = "\\s*([^,]*)=([^,]*)\\w*($|,)";
    private final String paramPatternWithParens = "^\\([\\s]*(.*)[\\s]*\\)$";

    MetricTokenIterator iterator;

    List<MetricDetail> metricDetails = new ArrayList<>();


    public MetricDetailParser(String input) {
        iterator = new MetricTokenIterator(input);
        while (iterator.hasNext()) {
            metricDetails.add(extractMetricDetail(iterator.next()));
        }
    }

    private MetricDetail extractMetricDetail(String input) {
        Matcher m = Pattern.compile(nameFromParentheticalPattern).matcher(input);
        if (!m.find()) {
            throw new IllegalArgumentException(
                    input + " could not be parsed with pattern " + nameFromParentheticalPattern)
            ;
        }
        String metricName = m.group(1);
        String baseParams = m.group(2);

        Map<String, String> params;

        if (baseParams.isEmpty()) {
            params = Collections.emptyMap();
        } else {
            Matcher m2 = Pattern.compile(paramPatternWithParens).matcher(baseParams);
            if (!m2.find() || m2.group(1).trim().isEmpty()) {
                params = Collections.emptyMap();
            } else {
                String parameters = m2.group(1);
                System.out.println("Parameters: " + parameters);
                params = extractParameter(parameters);
            }
        }
        return new MetricDetail(input, metricName, params);
    }

    private Map<String, String> extractParameter(String input) {
        Map<String, String> result = new HashMap<>();
        Matcher matcher = Pattern.compile(nameValuePairPattern).matcher(input);
        while (matcher.find()) {
            String group1 = matcher.group(1);
            if (group1.isEmpty()) {
                continue;
            }
            String key = matcher.group(1).trim();
            String value = matcher.group(2).trim();
            result.put(key, value);
        }
        return result;
    }

    public List<MetricDetail> getMetricDetails() {
        return metricDetails;
    }
}
