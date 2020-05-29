/*
 * Copyright 2010-2020 JetBrains s.r.o. and Kotlin Programming Language contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.resolve.calls.tower

/**
 * The annotation for marking classes for allowing resolver not to resolve extension
 * methods and properties in implicit context of this class instances.
 *
 * Resolving extensions, i.e., with [TowerResolver]
 * requires checking for extension presence in all available scopes, which makes this operation too
 * resource-consuming in scenarios with a big number of implicit receivers. In these scenarios, you
 * may want to mark classes which will be definitely not extended and whose instances appear as implicit
 * receivers, with this annotation.
 */
@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
annotation class AllowSkipExtensionsResolutionForImplicits
