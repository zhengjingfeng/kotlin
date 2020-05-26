/*
 * Copyright 2010-2020 JetBrains s.r.o. and Kotlin Programming Language contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.js.engine

interface ScriptEngine {
    fun eval(script: String): String
    fun evalVoid(script: String) {
        eval(script)
    }
    // TODO loadFiles
    fun loadFile(path: String)
    // TODO remove?
    fun release()

    fun saveState()
    fun restoreState()
}

interface ScriptEngineEx : ScriptEngine {
    fun <T> evaluate(script: String): T
}
