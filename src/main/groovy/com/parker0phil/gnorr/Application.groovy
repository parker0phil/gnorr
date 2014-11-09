package com.parker0phil.gnorr

import ratpack.handling.Context
import ratpack.handling.internal.DefaultRedirector
import ratpack.http.MutableHeaders
import ratpack.http.Response

/**
 * Massive hack to save reimplementing private DefaultRedirector.&generateRedirectLocation
 */

class Application {


        def locationHolder = ['Location': '']
        @Delegate Context context

    Application(Context context) {
            this.context = context
        }

        Response getResponse() {
            return [
                    status: {},
                    send: {},
                    getHeaders: { [set: locationHolder.&put] as MutableHeaders }
            ] as Response
        }

        def getLocation() {
            return locationHolder.Location
        }

    static uri(Context context, String path){
        Application dummyContext = new Application(context)
        new DefaultRedirector().redirect(dummyContext, path, 0)
        return dummyContext.location
    }
}


