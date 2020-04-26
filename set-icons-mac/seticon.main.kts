#!/usr/bin/env kotlin
//## Sample Script 0.1
//## Dependencies: notify-osd

import java.io.*

// brew install fileicon
//sudo snap install kotlin --classic

// https://github.com/sveinbjornt/osxiconutils
// https://github.com/mklement0/fileicon

val isMacOS = System.getProperty("os.name").toLowerCase().contains("mac")

fun File.exec(vararg args: String) {
    println(args.joinToString(" "))
    Runtime.getRuntime().exec(args, null, this).waitFor()
}
val cwd = File(".")

fun setFolderIcon(folder: File, icon: File) {
    if (isMacOS) {
        cwd.exec("fileicon", "set", folder.absolutePath, icon.absolutePath)
    } else {
        folder.exec("gio", "set", "-t", "string", "$folder", "metadata::custom-icon", "file://$icon")
    }
}

if (args.isEmpty()) {
    error("Must specify the folder to process or /install")
}

if (args[0] == "/install") {
    val outScript = File("${System.getenv("HOME")}/.local/share/nautilus/scripts", "seticon.main.kts")
    File("seticon.main.kts").copyTo(outScript, overwrite = true)
    outScript.setExecutable(true)
    System.exit(0)
}

val folder = File(args[0]).absoluteFile
if (!folder.isDirectory) {
    error("'$folder' is not a directory")
}


fun trySetFolderIcon(folder: File) {
    if (!folder.exists()) return
    val possibleIcons = listOf("icon.png", "icon.jpg", "poster.png", "poster.jpg", "poster.jpeg", "fanart.png", "fanart.jpg", "fanart.jpeg")
    val icon = possibleIcons.asSequence().map { File(folder, it) }.firstOrNull { it.exists() } ?: return
    setFolderIcon(folder, icon)
}

trySetFolderIcon(folder)
for (subdir in (folder.listFiles() ?: arrayOf()).filter { it.isDirectory }) {
    trySetFolderIcon(subdir)
}

fun displayNotification(message: String) {
    if (isMacOS) {
        cwd.exec("osascript", "-e", "display notification '$message'")
    } else {
        cwd.exec("notify-send", "seticon.main.kts", message)
    }
}

displayNotification("Processed $folder")
