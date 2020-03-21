#!/usr/bin/env kotlin
//## Sample Script 0.1
//## Dependencies: notify-osd

import java.io.File
import java.lang.management.ManagementFactory

//sudo snap install kotlin --classic

if (args.isEmpty()) {
    error("Must specify the folder to explore or /install")
}

val scriptsFolder = "${System.getenv("HOME")}/.local/share/nautilus/scripts"

if (args[0] == "/install") {
    val outScript = File(scriptsFolder, "seticon.main.kts")
    File("seticon.main.kts").copyTo(outScript, overwrite = true)
    outScript.setExecutable(true)
    System.exit(0)
}

val folder = File(args[0]).absoluteFile
if (!folder.isDirectory) {
    error("'$folder' is not a directory")
}

fun File.exec(vararg args: String) {
    println(args.joinToString(" "))
    Runtime.getRuntime().exec(args, null, this)
}

for (subdir in (folder.listFiles() ?: arrayOf()).filter { it.isDirectory }) {
    val possibleIcons = listOf("icon.png", "icon.jpg", "poster.png", "poster.jpg", "poster.jpeg", "fanart.png", "fanart.jpg", "fanart.jpeg")
    val icon = possibleIcons.asSequence().map { File(subdir, it) }.firstOrNull { it.exists() } ?: continue

    subdir.exec("gio", "set", "-t", "string", "$subdir", "metadata::custom-icon", "file://$icon")
}

folder.exec("notify-send", "seticon.main.kts", "Processed $folder")

