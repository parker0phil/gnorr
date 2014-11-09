package parker0phil

import groovy.json.JsonOutput
import groovy.json.JsonSlurper
import org.apache.http.HttpStatus
import ratpack.groovy.test.LocalScriptApplicationUnderTest
import ratpack.groovy.test.TestHttpClient
import ratpack.groovy.test.TestHttpClients
import ratpack.test.ApplicationUnderTest
import spock.lang.Specification

class GnorrShouldSpec extends Specification {
    ApplicationUnderTest app = new LocalScriptApplicationUnderTest()
    @Delegate TestHttpClient _ = TestHttpClients.testHttpClient(app)
    def json = new JsonSlurper()

    def "request to contract route returns contract overview"() {
        when:
        get "library"

        then:
        with(response) {
            statusCode == 200
            def resp = json.parseText body.asString()
            resp == json.parseText("""
            {
                "library" : {
                    "/library/book/*" : {"allowed_methods" : ["GET"]},
                    "/library/book/@" : {"allowed_methods" : ["GET", "PUT", "POST", "PATCH", "DELETE"]}
                }
            }
            """)
        }
    }

    def "Entity request with named identifier returns that entity"() {
        when:
        get "library/book/@name=Book2"

        then:
        with(response) {
            statusCode == 200
            def resp = json.parseText body.asString()
            resp.book.id == '2'
            resp.book.name == 'Book2'
        }
    }

    def "An existing entity can be updated with a PATCH"() {
        when:
        request.contentType "application/json"
        request.body JsonOutput.toJson([other: "bar"])
        patch "library/book/@11"

        then:
        with(response) {
            statusCode() == HttpStatus.SC_OK
            def resp = json.parseText body.asString()
            resp.book.id == '11'
            resp.book.name == 'Book11'
            resp.book.other == 'bar'
        }
    }
}