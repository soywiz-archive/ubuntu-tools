#!/usr/bin/env kotlin
//## Sample Script 0.1
//## Dependencies: notify-osd

import java.io.*
import java.nio.*

// https://github.com/sveinbjornt/osxiconutils
// https://github.com/mklement0/fileicon
// brew install fileicon

val isMacOS = System.getProperty("os.name").toLowerCase().contains("mac")

fun Int.toBEByteArray(): ByteArray = ByteBuffer.allocate(4).order(ByteOrder.BIG_ENDIAN).putInt(this).array()
fun Int.toLEByteArray(): ByteArray = ByteBuffer.allocate(4).order(ByteOrder.LITTLE_ENDIAN).putInt(this).array()

fun File.exec(vararg args: String) {
    println(args.joinToString(" "))
    Runtime.getRuntime().exec(args, null, this).waitFor()
}
val cwd = File(".")

// https://en.wikipedia.org/wiki/Apple_Icon_Image_format
fun createIcon(image: File): ByteArray {
    //val out = File("/tmp/out.jp2")
    val tmpPng = File("/tmp/out.png")
    //val outJp2 = File("/tmp/out.jp2")
    //val out = File("/tmp/out.jpg")
    //convert poster.jpg -quality 40 -thumbnail 512x512 poster.jp2
    //Runtime.getRuntime().exec(arrayOf("convert", image.absolutePath, "-quality", "40", "-resize", "512x512^", "-gravity", "center", "-extent", "512x512", out.absolutePath)).waitFor()
    //Runtime.getRuntime().exec(arrayOf("convert", image.absolutePath, "-quality", "40", "-resize", "512x512", "-gravity", "center", "-extent", "512x512", out.absolutePath)).waitFor()
    //Runtime.getRuntime().exec(arrayOf("convert", image.absolutePath, "-quality", "40", out.absolutePath)).waitFor()
    cwd.exec("convert", image.absolutePath, "-quality", "40", "-thumbnail", "512x512", tmpPng.absolutePath)
    //Runtime.getRuntime().exec(arrayOf("sips", "-s", "format", "jp2", tmpPng.absolutePath, "--out", outJp2.absolutePath)).waitFor()
    // sips -s format jp2 out.png --out out2.jp2
    val outBytes = tmpPng.readBytes()

    val baos = ByteArrayOutputStream()
    baos.write("icns".toByteArray(Charsets.UTF_8))
    baos.write((outBytes.size + 8 + 8).toBEByteArray())
    baos.write("ic09".toByteArray(Charsets.UTF_8))
    baos.write((outBytes.size + 8).toBEByteArray())
    baos.write(outBytes)
    return baos.toByteArray()
}

fun setFolderIcon(folder: File, icon: File) {
    /*
    folder.mkdirs()
    val iconFile = File(folder, "Icon\r")
    exec("touch", iconFile.absolutePath)
    iconFile.writeBytes(createIcon(icon))
    //iconFile.writeBytes(File("/tmp/AppIcon.icns").readBytes())
    exec("SetFile", "-a", "V", iconFile.absolutePath)
     */
    if (isMacOS) {
        cwd.exec("fileicon", "set", folder.absolutePath, icon.absolutePath)
    } else {
        folder.exec("gio", "set", "-t", "string", "$folder", "metadata::custom-icon", "file://$icon")
    }
}

//setFolderIcon(File("/tmp/1"), File("/tmp/poster.jpg"))
//File("/tmp/poster.icns").writeBytes(createIcon(File("/tmp/poster.jpg")))

/*
convert poster.jpg -quality 40 -thumbnail 512x512 poster.jp2
 */

//sudo snap install kotlin --classic

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
