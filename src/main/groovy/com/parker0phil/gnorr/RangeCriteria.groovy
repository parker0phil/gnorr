package com.parker0phil.gnorr


class RangeCriteria {

    public static String PATH_MATCHER = '\\*\\d*\\.\\.\\d*'

    private int from, to;

    private RangeCriteria(int from, int to) {
        this.from = from
        this.to = to
        if (from>to){
            throw new IllegalArgumentException("Range 'from' must be less than or equal 'to'")
        }
    }

    int getFromIndex() {
        return from -1
    }

    int getToIndex() {
        return to -1
    }

    int getFrom() {
        return from
    }

    int getTo() {
        return to
    }

    int getSize() {
        return (to - from) + 1
    }

    public static RangeCriteria from(String pathCriteria){
        def range = pathCriteria - '*'
        def (from,to) = range.tokenize('..').collect { it.toInteger() }
        new RangeCriteria(from, to)
    }
}
