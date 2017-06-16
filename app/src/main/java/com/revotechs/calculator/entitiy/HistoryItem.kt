package com.revotechs.calculator.entitiy

import java.io.Serializable

/**
 * Project Calculator
 * Created on 30.05.2017
 * @author CriticalGnome
 */
data class HistoryItem(
        var id: Long?,
        var date: String,
        var expression: String,
        var result: String,
        var comment: String?,
        var locked: Boolean) : Serializable
