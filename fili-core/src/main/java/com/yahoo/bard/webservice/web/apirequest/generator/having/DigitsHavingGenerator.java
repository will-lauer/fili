// Copyright 2020 Oath Inc.
// Licensed under the terms of the Apache license. Please see LICENSE.md file distributed with this work for terms.
package com.yahoo.bard.webservice.web.apirequest.generator.having;


import static com.yahoo.bard.webservice.web.ErrorMessageFormat.HAVING_INVALID_WITH_DETAIL;

import com.yahoo.bard.webservice.data.config.ConfigurationLoader;
import com.yahoo.bard.webservice.data.metric.LogicalMetric;
import com.yahoo.bard.webservice.data.metric.MetricDictionary;
import com.yahoo.bard.webservice.web.ApiHaving;
import com.yahoo.bard.webservice.web.BadApiRequestException;
import com.yahoo.bard.webservice.web.BadHavingException;
import com.yahoo.bard.webservice.web.havingparser.HavingsLex;
import com.yahoo.bard.webservice.web.havingparser.HavingsParser;

import org.antlr.v4.runtime.misc.ParseCancellationException;
import org.antlr.v4.runtime.tree.ParseTreeWalker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;
import java.util.Map;
import java.util.Set;

//import com.yahoo.bard.webservice.web.havingparser.HavingsLex;
//import com.yahoo.bard.webservice.web.havingparser.HavingsParser;


public class DigitsHavingGenerator extends DefaultHavingApiGenerator {
    private static final Logger LOG = LoggerFactory.getLogger(DigitsHavingGenerator.class);
    private final MetricDictionary metricDictionary;

    /**
     * Constructor.
     *
     * @param loader  Connects the resource dictionaries with the loaders..
     */
    public DigitsHavingGenerator(ConfigurationLoader loader) {
        super(loader);
        this.metricDictionary = loader.getMetricDictionary();
    }

    public Map<LogicalMetric, Set<ApiHaving>> apply(
        String havingQuery,
        Set<LogicalMetric> logicalMetrics
    ) throws BadApiRequestException {
        LOG.trace("Metrics from the query: {}", logicalMetrics);
        LOG.trace("Having query: {}", havingQuery);

        // Havings are optional hence check if havings are requested.
        if (havingQuery == null || "".equals(havingQuery)) {
            return Collections.emptyMap();
        }

        ApiHavingsListListener apiHavingsListListener = new ApiHavingsListListener(metricDictionary, logicalMetrics);
        HavingsLex lexer = HavingGrammarUtils.getLexer(havingQuery);
        HavingsParser parser = HavingGrammarUtils.getParser(lexer);
        try {
            try {
                HavingsParser.HavingsContext tree = parser.havings();
                ParseTreeWalker.DEFAULT.walk(apiHavingsListListener, tree);
            } catch (ParseCancellationException parseException) {
                LOG.debug(HAVING_INVALID_WITH_DETAIL.logFormat(havingQuery, parseException.getMessage()));
                throw new BadHavingException(
                    HAVING_INVALID_WITH_DETAIL.format(havingQuery, parseException.getMessage()),
                    parseException.getCause());
            }
            Map<LogicalMetric, Set<ApiHaving>> generated = apiHavingsListListener.getMetricHavingsMap();
            LOG.trace("Generated map of havings: {}", generated);
            return generated;
        } catch (BadHavingException havingException) {
            throw new BadApiRequestException(havingException.getMessage(), havingException);
        }
    }
}
