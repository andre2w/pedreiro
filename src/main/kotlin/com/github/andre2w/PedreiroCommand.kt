package com.github.andre2w

import io.micronaut.configuration.picocli.PicocliRunner
import picocli.CommandLine.*
import javax.inject.Inject

@Command(name = "pedreiro", description = ["..."],
        mixinStandardHelpOptions = true)
class PedreiroCommand : Runnable {

    @Parameters(paramLabel = "Blueprint",
            arity = "1",
            description = ["Name of the blueprint that you want built"])
    private var blueprintName : String = ""

    @Option(names = ["-a", "--arg"],
            description = ["Extra variables to be used in your template"],
            arity = "0..*")
    private var extraVariables : Map<String,String> = emptyMap()

    @Inject
    private lateinit var pedreiro : Pedreiro

    override fun run() {
        pedreiro.build(Arguments(blueprintName, extraVariables))
    }

    companion object {
        @JvmStatic fun main(args: Array<String>) {
            PicocliRunner.run(PedreiroCommand::class.java, *args)
        }
    }
}
