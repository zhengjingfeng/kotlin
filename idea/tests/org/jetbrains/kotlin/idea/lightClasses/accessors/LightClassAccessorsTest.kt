/*
 * Copyright 2010-2020 JetBrains s.r.o. and Kotlin Programming Language contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.idea.lightClasses.accessors;

import com.intellij.testFramework.LightProjectDescriptor
import org.jetbrains.kotlin.idea.test.KotlinLightCodeInsightFixtureTestCase
import org.jetbrains.kotlin.idea.test.KotlinWithJdkAndRuntimeLightProjectDescriptor
import org.jetbrains.kotlin.psi.namedDeclarationVisitor
import org.jetbrains.kotlin.test.JUnit3WithIdeaConfigurationRunner;
import org.junit.runner.RunWith;

@RunWith(JUnit3WithIdeaConfigurationRunner::class)
class LightClassAccessorsTest : KotlinLightCodeInsightFixtureTestCase() {
    fun testPropertyAccessor() {
        val file = myFixture.configureByText(
            "test.kt",
            //language=kotlin
            """
            class A {
                companion object {
                    val a: Int
                        get() = 5
                }
            }
            """.trimIndent()
        )
        file.accept(namedDeclarationVisitor { declaration ->
            println(declaration.text)
        })
        println("1")
        Thread.sleep(20000)
    }

    override fun getProjectDescriptor(): LightProjectDescriptor {
        return KotlinWithJdkAndRuntimeLightProjectDescriptor.INSTANCE
    }
}