package com.maryambehzi.myarea.UI

interface ILocationResult {
    fun areItemsSame(other: ILocationResult): Boolean
    fun areContentsSame(other: ILocationResult): Boolean
}