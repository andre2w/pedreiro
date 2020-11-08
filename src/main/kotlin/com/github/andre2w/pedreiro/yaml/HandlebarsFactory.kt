package com.github.andre2w.pedreiro.yaml

import com.github.jknack.handlebars.Handlebars
import com.github.jknack.handlebars.io.FileTemplateLoader
import javax.inject.Singleton

@Singleton
class HandlebarsFactory {

    fun withBaseFolder(baseFolder: String): Handlebars {
        val fileTemplateLoader = FileTemplateLoader(baseFolder, "")
        return Handlebars(fileTemplateLoader)
    }
}
