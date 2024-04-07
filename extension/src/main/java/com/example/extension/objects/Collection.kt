package com.example.extension.objects

import java.util.stream.Collectors

fun <T> Collection<T>?.notNullOrEmpty(): Boolean {
    return !this.isNullOrEmpty()
}

fun <T> List<T>.copy(): MutableList<T> {
    return if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
        this.stream().collect(Collectors.toList())
    } else {
        this.toMutableList()
    }
}

fun <T> List<T>?.join(collection: Collection<T>?): List<T>? {
    val list = mutableListOf<T>()
    if (!this.isNullOrEmpty()) {
        list.addAll(this)
    }
    if (!collection.isNullOrEmpty()) {
        list.addAll(collection)
    }
    return if (list.isEmpty()) return null else list
}

fun <T, R> List<T>?.convert(block: (T) -> R?): List<R>? {
    val list = mutableListOf<R>()
    this?.foreachCatching {
        val r = block(it)
        if (r != null) {
            list.add(r)
        }
    }
    if (list.isEmpty()) return null
    return list
}

fun <T, R> Iterable<T>?.convert(block: (T) -> R?): List<R>? {
    val list = mutableListOf<R>()
    this?.foreachCatching {
        val r = block(it)
        if (r != null) {
            list.add(r)
        }
    }
    if (list.isEmpty()) return null
    return list
}

fun <T, R> MutableIterator<T>?.convert(block: (T) -> R?): List<R>? {
    val list = mutableListOf<R>()
    this?.foreachCatching {
        val r = block(it)
        if (r != null) {
            list.add(r)
        }
    }
    if (list.isEmpty()) return null
    return list
}

fun <T> Iterable<T>.foreachCatching(block: (T) -> Unit) {
    for (item in this) try {
        block(item)
    } catch (e: Exception) {
        e.printStackTrace()
    }
}

fun <T> MutableIterator<T>.foreachCatching(block: (T) -> Unit) {
    for (item in this) try {
        block(item)
    } catch (e: Exception) {
        e.printStackTrace()
    }
}

/**
 * Typed T should be override method toString() : String
 */
fun <T> Collection<T>?.search(
    s: String?,
    searchProperty: ((T) -> String?) = { it?.toString() }
): List<T>? {
    return this?.filter { searchProperty(it).like(s) }
}


