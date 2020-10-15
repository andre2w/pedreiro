package com.github.andre2w.pedreiro.tasks

interface Task {
    fun execute()
}

data class Tasks(val tasks: List<Task>) {
    companion object {
        fun of(tasks: List<Task>) = Tasks(tasks)

        fun of(vararg tasks: Task) =
            Tasks(tasks.asList())
    }

    fun forEach(action: (Task) -> Unit) {
        tasks.forEach(action)
    }
}
