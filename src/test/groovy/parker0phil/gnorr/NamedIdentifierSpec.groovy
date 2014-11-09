package parker0phil.gnorr

import com.parker0phil.gnorr.NamedIdentifier
import spock.lang.Specification
import spock.lang.Unroll

class NamedIdentifierSpec extends Specification {

    @Unroll
    def "Regex matches well formed identifier string #identifier"(String identifier) {
        expect:
        identifier.matches(NamedIdentifier.PATH_MATCHER)

        where:
        identifier << [
                "@someVariable=someValue",
                "@v123=123",
                "@v123_abc=123",
                "@v123-zbc=123",
                "@someVariable-zbc=123_-!@£\$%^&**()",
                "@someVariable=some=Val:ue"
        ]
    }

    @Unroll
    def "Regex does not match badly formed identifier string #identifier"(String identifier) {
        expect:
        !identifier.matches(NamedIdentifier.PATH_MATCHER)

        where:
        identifier << [
                "someVariable=someValue",
                "@someVariable:someValue",
                "@someVariablesomeValue",
                "@123_abc=123" //leading digit in variable name
        ]
    }

    @Unroll
    def "Can parse Named Identifier #identifier"(String identifier, idName, idValue) {
        when:
        NamedIdentifier id = NamedIdentifier.from(identifier)

        then:
        id.identifierName == idName
        id.identifierValue == idValue

        where:
        identifier                            | idName             | idValue
        "@someVariable=someValue"             | 'someVariable'     | 'someValue'
        "@v123=123"                           | 'v123'             | '123'
        "@v123_abc=123"                       | 'v123_abc'         | '123'
        "@v123-abc=123"                       | 'v123-abc'         | '123'
        "@someVariable-zbc=123_-!@£\$%^&**()" | 'someVariable-zbc' | '123_-!@£$%^&**()'
        "@someVariable=some=Value"            | 'someVariable'     | 'some=Value'

    }
}