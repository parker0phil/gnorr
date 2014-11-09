package com.parker0phil.gnorr

import static ratpack.jackson.Jackson.json

class NumericalResponses {

    static initMetaclasses(){
        Integer.metaClass.call = { responseEntityClosure ->
            responseEntityClosure.owner.response.status delegate
            def responseEntity = responseEntityClosure()
            if (responseEntity){
                responseEntityClosure.owner.render(json(responseEntity))
            }else{
                responseEntityClosure.owner.response.send()
            }
        }

        Integer.metaClass.rightShift = { def locationClosure ->
            locationClosure.owner.redirect(delegate, locationClosure())
        }
        Integer.metaClass.rightShift = { def location, responseEntityClosure ->
            responseEntityClosure.owner.response.status delegate
            def responseEntity = responseEntityClosure()
            if (responseEntity){

            }else{
                responseEntityClosure.owner.redirect(delegate, location)
            }
        }
    }
}
