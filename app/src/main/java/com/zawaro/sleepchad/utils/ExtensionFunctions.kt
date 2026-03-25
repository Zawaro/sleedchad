package com.zawaro.sleepchad.utils

/**
 * Converts a set of day numbers (1-7) to a comma-separated string.
 * Days are sorted numerically before joining.
 */
fun Set<Int>.toDaysString(): String = 
    sorted().joinToString(",") { it.toString() }

/**
 * Converts a comma-separated string of day numbers back to a Set.
 * Empty strings and invalid numbers are filtered out.
 */
fun String.toDaysSet(): Set<Int> =
    split(",").filter { it.isNotEmpty() }.mapNotNull { it.toIntOrNull() }.toSet()
