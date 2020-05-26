/*
 * Copyright 2010-2020 JetBrains s.r.o. and Kotlin Programming Language contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.js.engine

import java.io.File

fun main() {
    val s = SimpleProcessBasedScriptEngine(File(System.getProperty("user.home") + "/.jsvu/v8"))
    do {
        print(">>> ")
        val t = readLine()

        if (t != null && t != "exit") {
            println(s.eval<String>(t))
        }
    } while (t != "exit")
}

class SimpleProcessBasedScriptEngine(
    private val vmExecFile: File
) : ScriptEngine {

    private var process: Process? = null

    private val buffer = ByteArray(1024)

    override fun <T> eval(script: String): T {
//        val t1 = System.currentTimeMillis()
        val vm = getVM()
//        println("$$$ vm t = " + (System.currentTimeMillis() - t1))

        val stdinStream = vm.outputStream
        val stdoutStream = vm.inputStream
        val stderrStream = vm.errorStream

        val writer = stdinStream.writer()
        writer.write(script.replace("\n", "\\n") + "\n")
        writer.flush()

        val sb = StringBuilder()

        var first = true

        val END = "\n<END>\n"
        while (vm.isAlive) {
            val n = stdoutStream.available()
            if (n == 0) continue

            val count = stdoutStream.read(buffer)

            if (first) {
                first = false
//                println("$$$ ready t = " + (System.currentTimeMillis() - t1))
            }


            val s = String(buffer, 0, count)
            sb.append(s)

            if (sb.endsWith(END)) break
        }

        return sb.removeSuffix(END).toString() as T // TODO
    }

    override fun evalVoid(script: String) {
        val result = eval<String>(script)
        if (result != "undefined") {
            error(result)
        }
    }

    override fun <T> callMethod(obj: Any, name: String, vararg args: Any?): T {
        TODO()
    }

    override fun loadFile(path: String) {
        evalVoid("load('$path')")
    }

    override fun release() {
        process?.destroy()
        process = null
    }

    override fun <T> releaseObject(t: T) {
        TODO()
    }

    override fun saveState() {
        evalVoid("saveState();")
    }

    override fun restoreState() {
        evalVoid("restoreState();")
    }

    private fun getVM(): Process {
        val p = process

        if (p != null && p.isAlive) return p

//        println("starting new process")

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
