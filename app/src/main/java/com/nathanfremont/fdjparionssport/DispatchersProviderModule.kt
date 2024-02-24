package com.nathanfremont.fdjparionssport

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
interface DispatchersProviderModule {
    @Binds
    fun bindDispatchersProvider(
        impl: DefaultDispatchersProvider
    ): IDispatchersProvider
}
