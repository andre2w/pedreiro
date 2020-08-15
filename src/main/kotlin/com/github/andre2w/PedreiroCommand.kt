package com.github.andre2w

import com.github.andre2w.pedreiro.Arguments
import com.github.andre2w.pedreiro.Pedreiro
import com.github.andre2w.pedreiro.blueprints.BlueprintParsingException
import com.github.andre2w.pedreiro.configuration.ConfigurationNotFound
import com.github.andre2w.pedreiro.environment.ConsoleHandler
import io.micronaut.configuration.picocli.PicocliRunner
import picocli.CommandLine.*
import javax.inject.Inject

@Command(
        name = "pedreiro", description = ["Pedreiro - A declarative scaffolding tool"],
        mixinStandardHelpOptions = true
)
class PedreiroCommand : Runnable {

    @Parameters(
            paramLabel = "Blueprint",
            arity = "1",
            description = ["Name of the blueprint that you want built"]
    )
    private var blueprintName: String = ""

    @Option(
            names = ["-a", "--arg"],
            description = ["Extra variables to be used in your template"],
            arity = "0..*"
    )
    private var extraVariables: Map<String, String> = emptyMap()

    @Option(
            names = ["-v", "--verbose"],
            description = [""]
    )
    private var verbose: Boolean = false

    @Inject
    lateinit var pedreiro: Pedreiro

    @Inject
    lateinit var consoleHandler: ConsoleHandler

    override fun run() {

        if (verbose)
            consoleHandler.activeDebugMode()

        try {
            pedreiro.build(Arguments(blueprintName, extraVariables, verbose))
            consoleHandler.print("Project created. You can start to work now.")
            consoleHandler.exitWith(0)
        } catch (err: BlueprintParsingException) {
            consoleHandler.printError(err.message ?: "Error while parsing blueprint")
            consoleHandler.exitWith(1)
        } catch (err: ConfigurationNotFound) {
            consoleHandler.printError("Failed to load configuration: ${err.configFilePath}")
            consoleHandler.exitWith(2)
        }
    }

    companion object {
        @JvmStatic fun main(args: Array<String>) {
            PicocliRunner.run(PedreiroCommand::class.java, *args)
        }
    }
}
