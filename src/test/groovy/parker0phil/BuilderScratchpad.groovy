package parker0phil

import groovy.transform.Canonical
import spock.lang.Specification

class BuilderScratchpad extends Specification {

    def "Can overide"(){
        when:
        def model = new ModelBuilder(id:"234", total:234).build()

        then:
        model.id == "234"
        model.total == 234
    }

    def "Can use defaults"(){
        when:
        def model = new ModelBuilder(id:"234").build()

        then:
        model.id == "234"
        model.total == 123
    }

    def "Can use defaults 2"(){
        when:
        def model = new ModelBuilder(total:234).build()

        then:
        model.id == "123"
        model.total == 234
    }

    class Model {
        String id
        int total

        Model(String id, int total) {
            this.id = id
            this.total = total
        }
    }

    @Canonical
    class ModelBuilder{

        String id = "123"
        int total = 123

        Model build(){
            new Model(id, total)
        }
    }
}