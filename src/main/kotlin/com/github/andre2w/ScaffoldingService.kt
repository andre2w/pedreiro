package com.github.andre2w

import com.github.andre2w.tasks.Task
import com.github.andre2w.tasks.Tasks
import javax.inject.Singleton

@Singleton
class ScaffoldingService {
    fun execute(tasks: Tasks) {
        tasks.forEach(Task::execute)
    }
}
