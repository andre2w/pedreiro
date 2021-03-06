package com.github.andre2w.pedreiro.blueprints

import com.github.andre2w.pedreiro.Arguments
import com.github.andre2w.pedreiro.configuration.ConfigurationManager
import com.github.andre2w.pedreiro.configuration.PedreiroConfiguration
import com.github.andre2w.pedreiro.environment.ConsoleHandler
import com.github.andre2w.pedreiro.environment.FileSystemHandler
import com.github.andre2w.pedreiro.environment.LocalEnvironment
import com.github.andre2w.pedreiro.environment.Platform
import com.github.andre2w.pedreiro.environment.ProcessExecutor
import com.github.andre2w.pedreiro.tasks.CreateFile
import com.github.andre2w.pedreiro.tasks.CreateFolder
import com.github.andre2w.pedreiro.tasks.ExecuteCommand
import com.github.andre2w.pedreiro.tasks.Tasks
import com.github.andre2w.pedreiro.yaml.YamlParser
import io.mockk.clearAllMocks
import io.mockk.every
import io.mockk.mockk
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.MethodSource
import org.junit.jupiter.params.provider.Arguments as JunitArguments

class BlueprintServiceShould {

    private val blueprintReader = mockk<BlueprintReader>()
    private val fileSystemHandler = mockk<FileSystemHandler>()
    private val environment = mockk<LocalEnvironment>()
    private val processExecutor = mockk<ProcessExecutor>()
    private val configurationManager = mockk<ConfigurationManager>()
    private val consoleHandler = mockk<ConsoleHandler>()
    private val configuration = PedreiroConfiguration("/home/user/pedreiro/.pedreiro/blueprints")
    private val taskFactory = TaskFactory(consoleHandler, fileSystemHandler, environment, processExecutor)
    private val blueprintService = BlueprintService(blueprintReader, YamlParser(), taskFactory)

    @BeforeEach
    fun setUp() {
        clearAllMocks()
        every { configurationManager.loadConfiguration() } returns configuration
    }

    @Test
    fun `parse blueprint that only create folders`() {
        val blueprintName = "blueprintName"
        val blueprint =
            """
        - type: folder
          name: project 
          children:
            - type: folder
              name: src
              children:
                 - type: folder
                   name: main
                   children:
                     - type: folder
                       name: kotlin
                     - type: folder
                       name: resources
            """.trimIndent()
        val arguments = Arguments(blueprintName)
        every { blueprintReader.read(arguments) } returns Blueprint(blueprint)

        val loadedTasks = blueprintService.loadBlueprint(arguments)

        val tasks = Tasks.of(
            listOf(
                CreateFolder(
                    "project",
                    fileSystemHandler,
                    environment,
                    consoleHandler
                ),
                CreateFolder(
                    "project/src",
                    fileSystemHandler,
                    environment,
                    consoleHandler
                ),
                CreateFolder(
                    "project/src/main",
                    fileSystemHandler,
                    environment,
                    consoleHandler
                ),
                CreateFolder(
                    "project/src/main/kotlin",
                    fileSystemHandler,
                    environment,
                    consoleHandler
                ),
                CreateFolder(
                    "project/src/main/resources",
                    fileSystemHandler,
                    environment,
                    consoleHandler
                )
            )
        )

        assertThat(loadedTasks).isEqualTo(tasks)
    }

    @Test
    fun `parse blueprint that has text files`() {
        val blueprintName = "blueprintName"
        val blueprint =
            """
        - type: folder
          name: project 
          children:
            - type: file
              name: build.gradle
              content: dependencies list
            """.trimIndent()
        val arguments = Arguments(blueprintName)
        every { blueprintReader.read(arguments) } returns Blueprint(blueprint)

        val loadedTasks = blueprintService.loadBlueprint(arguments)

        val tasks = Tasks.of(
            CreateFolder(
                "project",
                fileSystemHandler,
                environment,
                consoleHandler
            ),
            CreateFile(
                "project/build.gradle",
                "dependencies list",
                fileSystemHandler,
                environment,
                consoleHandler
            )
        )

        assertThat(loadedTasks).isEqualTo(tasks)
    }

    @Test
    fun `parse blueprint with command`() {
        val blueprintName = "blueprintWithCommand"
        val blueprint =
            """
            ---
            - type: folder
              name: test-command
              children:
                - type: command
                  command: gradle init
            """.trimIndent()
        val arguments = Arguments(blueprintName)
        every { blueprintReader.read(arguments) } returns Blueprint(blueprint)
        every { consoleHandler.currentPlatform() } returns Platform.WINDOWS

        val loadedTasks = blueprintService.loadBlueprint(arguments)

        val tasks = Tasks.of(
            CreateFolder(
                "test-command",
                fileSystemHandler,
                environment,
                consoleHandler
            ),
            ExecuteCommand(
                "gradle init",
                "test-command",
                processExecutor,
                environment
            )
        )

        assertThat(loadedTasks).isEqualTo(tasks)
    }

    @Test
    fun `throw exception when template is not valid`() {
        val blueprintName = "blueprintWithCommand"
        val blueprint = "\"INVALID:\":\":ASDF:"
        val arguments = Arguments(blueprintName)
        every { blueprintReader.read(arguments) } returns Blueprint(blueprint)

        assertThrows<BlueprintParsingException> { blueprintService.loadBlueprint(arguments) }
    }

    @Test
    internal fun `parse CreateFile using content from file in folder`() {
        val blueprint =
            """
            - type: file
              name: build.gradle
              source: build.gradle
            """.trimIndent()
        val arguments = Arguments("multifile-blueprint")
        val files = mapOf(
            "build.gradle" to "id 'kotlin'"
        )
        every { blueprintReader.read(arguments) } returns Blueprint(blueprint, files)

        val loadedTasks = blueprintService.loadBlueprint(arguments)

        val expectedTasks = Tasks.of(
            CreateFile(
                "build.gradle",
                "id 'kotlin'",
                fileSystemHandler,
                environment,
                consoleHandler
            )
        )
        assertThat(loadedTasks).isEqualTo(expectedTasks)
    }

    @ParameterizedTest
    @MethodSource("commandBlueprints")
    fun `retrieve command based on current platform`(blueprint: String, command: String, platform: Platform) {
        val arguments = Arguments("platform-blueprint")
        every { blueprintReader.read(arguments) } returns Blueprint(blueprint, emptyMap())
        every { consoleHandler.currentPlatform() } returns platform

        val loadedTasks = blueprintService.loadBlueprint(arguments)
        val expectedTasks = Tasks.of(
            ExecuteCommand(command, "", processExecutor, environment)
        )
        assertThat(loadedTasks).isEqualTo(expectedTasks)
    }

    companion object {
        @JvmStatic
        fun commandBlueprints() = listOf(
            JunitArguments.of("- type: command\n  win: win.bat\n  command: command.sh", "win.bat", Platform.WINDOWS),
            JunitArguments.of("- type: command\n  mac: mac.sh\n  command: command.sh", "mac.sh", Platform.MAC_OS),
            JunitArguments.of("- type: command\n  linux: linux.sh\n  command: command.sh", "linux.sh", Platform.LINUX),
            JunitArguments.of("- type: command\n  win: command.bat\n  command: command.sh", "command.sh", Platform.LINUX)
        )
    }
}
