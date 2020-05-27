/*
 * Copyright 2010-2020 JetBrains s.r.o. and Kotlin Programming Language contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.js.engine

import java.io.File

private val isWindows = "win" in System.getProperty("os.name").toLowerCase()
private val LINE_SEPARATOR = System.getProperty("line.separator")!!

class SimpleProcessBasedScriptEngine(
    private val vmExecFile: File
) : ScriptEngine {

    private var process: Process? = null
    private val buffer = ByteArray(1024)

    override fun eval(script: String): String {
        val vm = getVM()

        val stdin = vm.outputStream
        val stdout = vm.inputStream
        val stderr = vm.errorStream

        val writer = stdin.writer()
        writer.write(script.replace("\n", "\\n") + "\n")
        writer.flush()

        val out = StringBuilder()

        val END = "<END>$LINE_SEPARATOR"
        while (vm.isAlive) {
            val n = stdout.available()
            if (n == 0) continue

            val count = stdout.read(buffer)

            val s = String(buffer, 0, count)
            out.append(s)

            if (out.endsWith(END)) break
        }

        if (stderr.available() > 0) {
            val err = StringBuilder()

            while(vm.isAlive && stderr.available() > 0) {
                val count = stderr.read(buffer)
                err.append(String(buffer, 0, count))
                if (count <= 0) break
            }

            error("ERROR:\n$err\nOUTPUT:\n$out")
        }

        return out.removeSuffix(END).removeSuffix("\n").toString()
    }

    override fun loadFile(path: String) {
        eval("load('${path.replace('\\', '/')}');")
    }

    override fun release() {
        process?.destroy()
        process = null
    }

    override fun saveState() {
        eval("saveState();")
    }

    override fun restoreState() {
        eval("restoreState();")
    }

    private fun getVM(): Process {
        val p = process

        if (p != null && p.isAlive) return p

        process = null

        val executablePath = vmExecFile.absolutePath + if (isWindows) ".cmd" else ""

        val builder = ProcessBuilder(
            executablePath,
            "-f",
            "js/js.engines/src/org/jetbrains/kotlin/js/engine/repl.js",
        )
        return builder.start().also {
            process = it
        }
    }
}

/*
            v8   sm  jsc
load         +    +    +
print        +    +    +
printErr     +    +    +
read         +    +    +
readline     +    +    +
readbuffer   +    -    -
quit         +    +    +
*/

// TODO check on windows

//interface JsScriptEngine {
//    fun eval(code: String): String
//    fun loadFiles(vararg paths: String)
//    // loadFiles ["aaa", "bbb"]
//    // run test ["foo", "bar", "box"]
//    // push state
//    // pop state
//    // something for console?
//    // eval?
//    // print raw data
//    //
//}
//

fun main() {
    val s = SimpleProcessBasedScriptEngine(File(System.getProperty("user.home") + "/.jsvu/v8"))
    do {
        print(">>> ")
        val t = readLine()

        if (t != null && t != "exit") {
            println(s.eval(t))
        }
    } while (t != "exit")
}
