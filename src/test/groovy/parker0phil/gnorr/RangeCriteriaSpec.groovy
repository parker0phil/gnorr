package parker0phil.gnorr

import com.parker0phil.gnorr.RangeCriteria
import spock.lang.Specification
import spock.lang.Unroll

class RangeCriteriaSpec extends Specification {

    @Unroll
    def "Regex matches well formed criteria string #criteria"(String criteria) {
        expect:
        criteria.matches(RangeCriteria.PATH_MATCHER)

        where:
        criteria << [
                "*10..1",
                "*0..0"
        ]
    }

    @Unroll
    def "Regex does not match badly formed criteria string #criteria"(String criteria) {
        expect:
        !criteria.matches(RangeCriteria.PATH_MATCHER)

        where:
        criteria << [
                "1..10",
                "*0.0",
                "*a..2",
                "*1..a"
        ]
    }

    @Unroll
    def "Can parse Range Criteria #criteria"(String criteria, from, to, size, fromIndex, toIndex) {
        when:
        RangeCriteria range = RangeCriteria.from(criteria)

        then:
        range.from == from
        range.to == to
        range.size == size
        range.fromIndex == fromIndex
        range.toIndex == toIndex

        where:
        criteria | from | to | size | fromIndex | toIndex
        '*1..10' | 1    | 10 | 10   | 0         | 9
        '*1..1'  | 1    | 1  | 1    | 0         | 0
    }

    @Unroll
    def "Complains with invalid range #criteria"(String criteria) {
        when:
        RangeCriteria.from(criteria)

        then:
        thrown(IllegalArgumentException)

        where:
        criteria << [
                "*10..1" //numbers our of order
        ]
    }


}