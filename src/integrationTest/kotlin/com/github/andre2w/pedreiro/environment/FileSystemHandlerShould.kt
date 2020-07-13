package com.github.andre2w.pedreiro.environment

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import java.nio.file.Files
import java.nio.file.Paths
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class FileSystemHandlerShould {

    private val fileContent =
            "---" + System.lineSeparator() +
                    "- name: project" + System.lineSeparator() +
                    "type: folder" + System.lineSeparator() +
                    "children:" + System.lineSeparator() +
                    "- name: build.gradle" + System.lineSeparator() +
                    "type: file" + System.lineSeparator() +
                    "content: |" + System.lineSeparator() +
                    "apply plugin: kotlin"

    private val fileSystemHandler = FileSystemHandler()

    @Test
    fun `create file with contents`() {
        val filePath = "${System.getProperty("user.dir")}/temptestfile-${rightNow()}.txt"
        val path = Paths.get(filePath)
        fileSystemHandler.createFile(filePath, fileContent)

        val readString = String(Files.readAllBytes(path))
        assertThat(readString).isEqualTo(fileContent)

        Files.delete(path)
    }

    @Test
    fun `read file contents`() {
        val filePath = "${System.getProperty("user.dir")}/temptestfile-${rightNow()}.txt"
        val path = Paths.get(filePath)
        Files.write(path, fileContent.toByteArray())

        val readContent = fileSystemHandler.readFile(filePath)

        assertThat(readContent).isEqualTo(fileContent)
        Files.delete(path)
    }

    @Test
    fun `create folder`() {
        val folderPath = "${System.getProperty("user.dir")}/test-folder-${rightNow()}"

        fileSystemHandler.createFolder(folderPath)

        assertThat(Files.exists(Paths.get(folderPath))).isTrue()
        Files.delete(Paths.get(folderPath))
    }

    @Test
    fun `return null when file is not found`() {
        val filePath = "${System.getProperty("user.dir")}/temptestfile-${rightNow()}.txt"

        val readContent = fileSystemHandler.readFile(filePath)

        assertThat(readContent).isNull()
    }

    @Test
    internal fun `check if path is a folder`() {
        val folderPath = "${System.getProperty("user.dir")}/test-folder-${rightNow()}"
        Files.createDirectory(Paths.get(folderPath))

        assertThat(fileSystemHandler.isFolder(folderPath)).isTrue()

        Files.delete(Paths.get(folderPath))
    }

    @Test
    internal fun `list file names inside a folder`() {
        val folderPath = "${System.getProperty("user.dir")}/test-folder-${rightNow()}"
        Files.createDirectory(Paths.get(folderPath))
        Files.write(Paths.get("$folderPath/test.txt"), fileContent.toByteArray())

        val filesInFolder = fileSystemHandler.listFilesIn(folderPath)

        assertThat(filesInFolder).isEqualTo(listOf("test.txt"))

        Files.delete(Paths.get("$folderPath/test.txt"))
        Files.delete(Paths.get(folderPath))
    }

    private fun rightNow(): String {
        val format = DateTimeFormatter.ofPattern("YYYY-MM-dd-hhmmssSSS")
        return LocalDateTime.now().format(format)
    }

}