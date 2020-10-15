package com.github.andre2w.pedreiro

import com.github.andre2w.pedreiro.blueprints.BlueprintService
import com.github.andre2w.pedreiro.tasks.Task
import javax.inject.Singleton

@Singleton
class Pedreiro(
    private val blueprintService: BlueprintService
) {

    fun build(arguments: Arguments) {
        val tasks = blueprintService.loadBlueprint(arguments)
        tasks.forEach(Task::execute)
    }
}
