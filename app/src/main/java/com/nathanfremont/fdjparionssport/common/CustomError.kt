package com.nathanfremont.fdjparionssport.common

import com.nathanfremont.fdjparionssport.common.NativeText
import java.util.UUID

data class CustomError(
    private val uuid: UUID = UUID.randomUUID(),
    val errorTitle: NativeText,
)