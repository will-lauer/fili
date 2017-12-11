package com.yahoo.bard.webservice.util

import org.joda.time.Interval

import spock.lang.Specification

/**
 * A collection of utility methods to aid in testing functionality that relies on JodaTime intervals.
 */
class IntervalTestingUtilsSpec extends Specification {
    /**
     * Returns a list of JodaTime intervals constructed from a list of string intervals.
     *
     * @param intervals  The list of string intervals
     * @return the list of JodaTime intervals
     */
    static List buildIntervalList(Collection<String> intervals) {
        intervals.collect { new Interval(it) }
    }

    /**
     * Return a list of JodaTime intervals, wrapped inside a SimplifiedIntervalList, from a list of string intervals.
     *
     * @param intervals  The list of string intervals
     * @return the list of JodaTime intervals wrapped inside a SimplifiedIntervalList
     */
    static SimplifiedIntervalList buildSimplifiedIntervalList(Collection<String> intervals) {
        return buildIntervalList(intervals) as SimplifiedIntervalList
    }

    SimplifiedIntervalList buildIntervalList(String start, String end) {
        new SimplifiedIntervalList([buildInterval(start, end)])
    }
}
