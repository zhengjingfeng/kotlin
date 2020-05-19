/*
 * Copyright 2010-2020 JetBrains s.r.o. and Kotlin Programming Language contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package templates.tests

import templates.*
import templates.Family.*

object AggregatesTest : TestTemplateGroupBase() {

    val f_minBy = test("minBy()") {
        includeDefault()
        include(ArraysOfUnsigned)
    } builder {

        body {
            """
            assertEquals(null, ${of()}.minBy { it })
            assertEquals(ONE, ${of(1)}.minBy { it })
            assertEquals(TWO, ${of(3, 2)}.minBy { it * it })
            assertEquals(THREE, ${of(3, 2)}.minBy { "a" })
            assertEquals(TWO, ${of(3, 2)}.minBy { it.toString() })
            """
        }
        bodyAppend(Iterables, Sequences, ArraysOfObjects, ArraysOfPrimitives) {
            """
            assertEquals(THREE, ${of(2, 3)}.minBy { -it })
            """
        }
        bodyAppend(Iterables, Sequences, ArraysOfObjects) {
            """
            assertEquals('b', $of('a', 'b').maxBy { "x${"$"}it" })
            assertEquals("abc", $of("b", "abc").maxBy { it.length })
            """
        }

        bodyAppend(ArraysOfPrimitives, PrimitiveType.Long) {
            """
            assertEquals(2000000000000, longArrayOf(3000000000000, 2000000000000).minBy { it + 1 })
            """
        }
        body(ArraysOfPrimitives, PrimitiveType.Boolean) {
            """
            assertEquals(true, booleanArrayOf(true, false).maxBy { it.toString() })
            assertEquals(false, booleanArrayOf(true, false).maxBy { it.toString().length })
            """
        }
        body(ArraysOfPrimitives, PrimitiveType.Char) {
            """
            assertEquals('b', charArrayOf('a', 'b').maxBy { "x${"$"}it" })
            assertEquals('a', charArrayOf('a', 'b').maxBy { "${"$"}it".length })
            """
        }
    }

    val f_minWith = test("minWith()") {
        includeDefault()
        include(ArraysOfUnsigned)
        exclude(PrimitiveType.Boolean, PrimitiveType.Char)
    } builder {

        body {
            """
            assertEquals(null, ${of()}.minWith(naturalOrder()))
            assertEquals(ONE, ${of(1)}.minWith(naturalOrder()))
            assertEquals(${literal(4)}, ${of(2, 3, 4)}.minWith(compareBy { it % ${literal(4)} }))
            """
        }
        bodyAppend(Iterables, Sequences, ArraysOfObjects) {
            """
            assertEquals("a", $of("a", "B").minWith(STRING_CASE_INSENSITIVE_ORDER))
            """
        }
    }

    val f_foldIndexed = test("foldIndexed()") {
        includeDefault()
        include(ArraysOfUnsigned)
        exclude(PrimitiveType.Boolean, PrimitiveType.Char)
    } builder {

        body {
            """
            expect(${literal(8)}) { ${of(1, 2, 3)}.foldIndexed(ZERO) { i, acc, e -> acc + i.to$P() * e } }
            expect(10) { ${of(1, 2, 3)}.foldIndexed(1) { i, acc, e -> acc + i + e.toInt() } }
            expect(${literal(15)}) { ${of(1, 2, 3)}.foldIndexed(ONE) { i, acc, e -> acc * (i.to$P() + e) } }
            expect(" 0-${toString(1)} 1-${toString(2)} 2-${toString(3)}") { ${of(1, 2, 3)}.foldIndexed("") { i, acc, e -> "${"$"}acc ${"$"}i-${"$"}e" } }
            
            expect(${literal(42)}) {
                val numbers = ${of(1, 2, 3, 4)}
                numbers.foldIndexed(ZERO) { index, a, b -> index.to$P() * (a + b) }
            }
    
            expect(ZERO) {
                val numbers = ${of()}
                numbers.foldIndexed(ZERO) { index, a, b -> index.to$P() * (a + b) }
            }
    
            expect("${toString(1)}${toString(1)}${toString(2)}${toString(3)}${toString(4)}") {
                val numbers = ${of(1, 2, 3, 4)}
                numbers.map { it.toString() }.foldIndexed("") { index, a, b -> if (index == 0) a + b + b else a + b }
            }
            """
        }
    }

    val f_foldRightIndexed = test("foldRightIndexed()") {
        include(Lists, ArraysOfObjects, ArraysOfPrimitives, ArraysOfUnsigned)
        exclude(PrimitiveType.Boolean, PrimitiveType.Char)
    } builder {

        body {
            """
            expect(${literal(8)}) { ${of(1, 2, 3)}.foldRightIndexed(ZERO) { i, e, acc -> acc + i.to$P() * e } }
            expect(10) { ${of(1, 2, 3)}.foldRightIndexed(1) { i, e, acc -> acc + i + e.toInt() } }
            expect(${literal(15)}) { ${of(1, 2, 3)}.foldRightIndexed(ONE) { i, e, acc -> acc * (i.to$P() + e) } }
            expect(" 2-${toString(3)} 1-${toString(2)} 0-${toString(1)}") { ${of(1, 2, 3)}.foldRightIndexed("") { i, e, acc -> "${"$"}acc ${"$"}i-${"$"}e" } }
            
            expect("${toString(1)}${toString(2)}${toString(3)}${toString(4)}3210") {
                val numbers = ${of(1, 2, 3, 4)}
                numbers.map { it.toString() }.foldRightIndexed("") { index, a, b -> a + b + index }
            }
            """
        }
    }
}
