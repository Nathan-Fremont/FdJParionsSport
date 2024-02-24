package com.nathanfremont.fdjparionssport

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import javax.inject.Inject

class DefaultDispatchersProvider @Inject constructor(
) : IDispatchersProvider {
    override val io: CoroutineDispatcher
        get() = Dispatchers.IO

    override val computation: CoroutineDispatcher
        get() = Dispatchers.Default

    override val ui: CoroutineDispatcher
        get() = Dispatchers.Main
}
