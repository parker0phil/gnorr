package parker0phil

import groovy.json.JsonSlurper
import ratpack.groovy.test.LocalScriptApplicationUnderTest
import ratpack.groovy.test.TestHttpClient
import ratpack.groovy.test.TestHttpClients
import ratpack.test.ApplicationUnderTest
import spock.lang.Specification

class GnorrMaySpec extends Specification {
    ApplicationUnderTest app = new LocalScriptApplicationUnderTest()
    @Delegate TestHttpClient _ = TestHttpClients.testHttpClient(app)
    def json = new JsonSlurper()

    def "Default criteria (that can't be supported) redirects to explicit range criteria"() {
        when:
        request.redirects().follow false
        get "library/book/*"

        then:
        with(response) {
            statusCode == 307
            header('Location').endsWith 'library/book/*1..10'
        }
    }
}