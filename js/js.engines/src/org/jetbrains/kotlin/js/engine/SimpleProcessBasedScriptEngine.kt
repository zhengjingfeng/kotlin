/*
 * Copyright 2010-2020 JetBrains s.r.o. and Kotlin Programming Language contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.js.engine

import java.io.File

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

        val sb = StringBuilder()

        val END = "\n<END>\n"
        while (vm.isAlive) {
            val n = stdout.available()
            if (n == 0) continue

            val count = stdout.read(buffer)

            val s = String(buffer, 0, count)
            sb.append(s)

            if (sb.endsWith(END)) break
        }

        if (stderr.available() > 0) {
            val err = StringBuilder()

            while(vm.isAlive) {
                val count = stdout.read(buffer)
                err.append(String(buffer, 0, count))
                if (count <= 0) break
            }

            error(err.toString())
        }

        return sb.removeSuffix(END).toString()
    }

    override fun loadFile(path: String) {
        eval("load('$path')")
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

        val builder = ProcessBuilder(
            vmExecFile.absolutePath,
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
