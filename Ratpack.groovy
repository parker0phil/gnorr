import com.parker0phil.gnorr.Application
import com.parker0phil.gnorr.NamedIdentifier
import com.parker0phil.gnorr.NumericalResponses
import com.parker0phil.gnorr.RangeCriteria
import ratpack.jackson.JacksonModule

import static ratpack.groovy.Groovy.ratpack
import static ratpack.jackson.Jackson.jsonNode

NumericalResponses.initMetaclasses()

ratpack {
    def bookGen = {
        (1..11).collect { [id: "$it".toString(), name: "Book$it".toString(), other: 'foo'] }
    }
    def books = bookGen()

    bindings {
        add new JacksonModule()
    }

    handlers {

        prefix('library') {

            get {
                return 200 {
                    [
                            library: [
                                    "/library/book/*": [allowed_methods: ['GET']],
                                    "/library/book/@": [allowed_methods: ['GET', 'PUT', 'POST', 'PATCH', 'DELETE']]
                            ]
                    ]
                }
            }

            prefix('book') {
                get('*') {
                    return 307 >> { '/library/book/*1..10' }
                }

                get(":criteria:${RangeCriteria.PATH_MATCHER}") {
                    RangeCriteria.from(pathTokens.get('criteria')).with {
                        return 200 {
                            [
                                    book: books[fromIndex..toIndex],
                                    range: [from: from, to: to, size: size, total: books.size()]
                            ]
                        }
                    }
                }

                get(":namedIdentifier:${NamedIdentifier.PATH_MATCHER}") {
                    def namedId = NamedIdentifier.from(pathTokens.get('namedIdentifier'))
                    def foundId = books.find { "${it[namedId.identifierName]}" == "$namedId.identifierValue" }?.id
                    return 307 >> { "/library/book/@$foundId" }
                }

                handler(':identifier:@.*') {
                    String identifier = pathTokens.get('identifier') - '@'
                    def requestBody = parse jsonNode()
                    byMethod {
                        get {
                            def book = books.find { "${it.id}" == identifier }
                            return (book) ? 200 { [book: book] } : 404 {}
                        }
                        put {
                            if (books.find { "${it.id}" == identifier }) {
                                return 409 >> { "/library/book/@$identifier" }
                            }
                            books << [id: identifier, name: requestBody.get('name')]
                            response.headers.add('Location', Application.uri(context, "/library/book/@$identifier"))
                            return 201 { [book: books.find { "$it.id" == identifier }] }
                        }
                        post {
                            def book = books.find { "${it.id}" == identifier }
                            if (book) {
                                book.name = requestBody.get('name')
                                return 200 { [book: book] }
                            }
                            return 404 {}
                        }
                        patch {
                            def book = books.find { "${it.id}" == identifier }
                            if (book) {
                                book.other = requestBody.get('other')
                                return 200 { [book: book] }
                            }
                            return 404 {}
                        }
                        delete {
                            books.removeAll { "${it.id}" == identifier }
                            return 200 {}
                        }
                    }
                }
            }
        }
        handler {
            return 405 { [allowed_methods: []] }
        }
    }
}