package com.github.andre2w.pedreiro

import com.github.andre2w.pedreiro.blueprints.BlueprintService
import javax.inject.Singleton

@Singleton
class Pedreiro(
        private val blueprintService: BlueprintService,
        private val scaffoldingService: ScaffoldingService
) {

    fun build(arguments: Arguments) {
        val tasks = blueprintService.loadBlueprint(arguments)
        scaffoldingService.execute(tasks)
    }

}