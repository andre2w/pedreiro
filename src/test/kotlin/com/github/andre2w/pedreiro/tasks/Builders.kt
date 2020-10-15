package com.github.andre2w.pedreiro.tasks

fun Tasks.Companion.of(vararg tasks: Task) =
    Tasks(tasks.asList())
