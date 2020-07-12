package com.github.andre2w.pedreiro

import com.github.andre2w.pedreiro.tasks.Task
import com.github.andre2w.pedreiro.tasks.Tasks
import javax.inject.Singleton

@Singleton
class ScaffoldingService {
    fun execute(tasks: Tasks) {
        tasks.forEach(Task::execute)
    }
}
