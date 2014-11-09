package parker0phil.gnorr.script

import spock.lang.Specification


/**
 * Created with IntelliJ IDEA.
 * User: phil
 * Date: 22/10/2014
 * Time: 09:19
 * To change this template use File | Settings | File Templates.
 */
class ScriptPlayground extends Specification {
    Gnorr gnorr = new Gnorr()

    def"Just playing with Syntax"(){
        gnorr.component ("library") {
            entity ("customer") * { foo() }
            entity ("customer") & { bar() }


            }
        }



    class Gnorr {
        def component(String componentName,  @DelegatesTo(GnorrComponent) Closure foo){
            return new GnorrComponent()
        }
    }
    class GnorrComponent{
        def entity(String entityName){
            return new GnorrEntity()
        }
    }
    class GnorrEntity{

        def multiply(@DelegatesTo(GnorrEntityCriteria) Closure cl){

        }

        def and(@DelegatesTo(GnorrEntityLifecycle) Closure cl){

        }
    }

    class GnorrEntityCriteria{

        def foo(){}
     }

     class GnorrEntityLifecycle{
        def bar(){}

     }

}
