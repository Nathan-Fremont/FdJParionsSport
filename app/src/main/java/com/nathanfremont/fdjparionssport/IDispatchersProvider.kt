package com.nathanfremont.fdjparionssport

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers

/**
 * Provides [CoroutineDispatcher]s for different kinds of operations.
 **/
interface IDispatchersProvider {
    /**
     * CoroutineDispatcher to run IO operations (disk, bluetooth, HTTP and such).
     * Most of the time, you want data transfer on a background thread by passing this [CoroutineDispatcher].
     *
     * Equivalent of kotlin's [Dispatchers.IO].
     * */
    val io: CoroutineDispatcher

    /**
     * Scheduler to run computationally heavy operations (algorithm on large dataset and such).
     * Most of the time, you want data computation on a background thread by passing this
     * [CoroutineDispatcher].
     *
     * Equivalent of kotlin's [Dispatchers.Default].
     * */
    val computation: CoroutineDispatcher

    /**
     * Scheduler to run operations on main thread (UI).
     * Most of the time, you want data to be observed from the UI by passing this [CoroutineDispatcher].
     *
     * Equivalent of kotlin's [Dispatchers.Main].
     * */
    val ui: CoroutineDispatcher
}
