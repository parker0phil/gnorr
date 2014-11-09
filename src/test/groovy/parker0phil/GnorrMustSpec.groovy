package parker0phil

import groovy.json.JsonOutput
import groovy.json.JsonSlurper
import org.apache.http.HttpStatus
import ratpack.groovy.test.LocalScriptApplicationUnderTest
import ratpack.groovy.test.TestHttpClient
import ratpack.groovy.test.TestHttpClients
import ratpack.test.ApplicationUnderTest
import spock.lang.Specification
import spock.lang.Unroll

class GnorrMustSpec extends Specification {
    ApplicationUnderTest app = new LocalScriptApplicationUnderTest()
    @Delegate TestHttpClient _ = TestHttpClients.testHttpClient(app)
    def json = new JsonSlurper()

    /**
     * '*' - criteria
     * '@' - identity
     *
     * list resources (simple criteria)
     * GET /resource and /resource/ are alias for GET /resource/*
     * GET /resource/* should return whole collection OR redirect (303 or 307?) to t
     *
     * instance resources
     * GET /resource/123 is an alias for GET /resource/@123 (default identifier)
     * GET /resource/
     *
     * POST /resource/  (creates a resource with provider determined identifier)
     * PUT /resource/@123  (creates a resource with consumer determined identifier)
     * POST /resource/@123 (updates a resource)
     * PATCH /resource/@123 (alternative to POST /resource @123)
     *
     * search resources (complex criteria)
     * POST /resource/* creates a criteria. Should either:
     *  200 with the results in the response body
     *  204 with a Location header to /resource/*@123 (which
     */
    @Unroll
    def "Non existent template (GET /#path) returns 405"(path) {
        when:
        get path

        then:
        with(response) {
            statusCode == 405
            body.jsonPath().get('allowed_methods').isEmpty()
        }

        where:
        path << [
                "doesnotexist",
                "library/doesnotexist"
        ]
    }

    def "Range criteria returns that range"() {
        when:
        get "library/book/*1..10"

        then:
        with(response) {
            statusCode == 200
            def resp = json.parseText body.asString()
            resp.book.size() == 10
            resp.range.from == 1
            resp.range.size == 10
            resp.range.to == 10
            resp.range.total == 11
        }
    }

    def "Entity request with default identifier returns that entity with a 200"() {
        when:
        get "library/book/@1"

        then:
        with(response) {
            statusCode == HttpStatus.SC_OK
            def resp = json.parseText body.asString()
            resp.book?.id == "1"
            resp.book?.name == 'Book1'
        }
    }

    def "Entity request with a named identifier redirects to the default identifier"() {
        when:
        request.redirects().follow false
        get "library/book/@name=Book1"

        then:
        with(response) {
            statusCode == 307
            header('Location').endsWith 'library/book/@1'
        }
    }

    def "Entity request that doesn't find an entity returns 404"() {
        when:
        get "library/book/@99"

        then:
        with(response) {
            statusCode == HttpStatus.SC_NOT_FOUND
            //TODO: Suggest error message as long as not information leakage
        }
    }




    def "A new entity can be PUT"() {
        when:
        request.contentType "application/json"
        request.body JsonOutput.toJson([name: "Book12", other: 'foo'])
        put "library/book/@12"

        then:
        with(response) {
            statusCode == HttpStatus.SC_CREATED
            header('Location').endsWith 'library/book/@12'
            def resp = json.parseText body.asString()
            resp.book?.id == '12'
            resp.book?.name == 'Book12'
        }
    }

    def "Entity can not be rePUT unless idempotent"() {
        when:
        request.contentType "application/json"
        request.body JsonOutput.toJson([name: "Book12", other: 'foo'])
        put "library/book/@1"

        then:
        with(response) {
            statusCode == HttpStatus.SC_CONFLICT
            header('Location').endsWith 'library/book/@1'
            //TODO: conflict information in response body (MAY spec)
        }
    }

    def "An existing entity can be replaced with a POST"() {
        when:
        request.contentType "application/json"
        request.body JsonOutput.toJson([name: "BookEleven", other: 'foo'])
        post "library/book/@11"

        then:
        with(response) {
            statusCode == HttpStatus.SC_OK
            def resp = json.parseText body.asString()
            resp.book?.id == '11'
            resp.book?.name == 'BookEleven'
            resp.book?.other == 'foo'
        }
    }

    def "Update POST to a non-existent entity returns 404"() {
        when:
        request.contentType "application/json"
        request.body JsonOutput.toJson([name: "BookEleven", other: 'foo'])
        post "library/book/@99"

        then:
        with(response) {
            statusCode == HttpStatus.SC_NOT_FOUND
            //TODO: Suggest error message as long as not information leakage
        }
    }

    def "Update PATCH to a non-existent entity returns 404"() {
        when:
        request.contentType "application/json"
        request.body JsonOutput.toJson([name: "BookEleven", other: 'bar'])
        patch "library/book/@99"

        then:
        with(response) {
            statusCode == HttpStatus.SC_NOT_FOUND
            //TODO: Suggest error message as long as not information leakage
        }
    }

    def "An existing entity can be removed with a DELETE"() {
        when:
        delete "library/book/@11"

        then:
        with(response) {
            statusCode == HttpStatus.SC_OK
            get("library/book/@11").statusCode == HttpStatus.SC_NOT_FOUND
        }
    }

    def "DELETE is idempotent"() {
        given:
        delete "library/book/@11"

        when:
        delete "library/book/@11"

        then:
        with(response) {
            statusCode == HttpStatus.SC_OK
            get("library/book/@11").statusCode == HttpStatus.SC_NOT_FOUND
        }
    }
}