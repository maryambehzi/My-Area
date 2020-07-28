package com.maryambehzi.myarea

interface ILocationResult {
    fun areItemsSame(other: ILocationResult): Boolean
    fun areContentsSame(other: ILocationResult): Boolean
}