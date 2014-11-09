package com.parker0phil.gnorr

import com.github.fge.jsonschema.core.report.ProcessingReport
import ratpack.parse.Parse

abstract class JsonSchema {
    public static Parse<ProcessingReport, NoOptions> jsonSchema() {
        return Parse.<ProcessingReport, NoOptions> of(ProcessingReport, new NoOptions());
    }


    static class NoOptions {

    }
}