package com.revotechs.calculator.entities

import java.io.Serializable

class HistoryItem(
        var id: Long?,
        var date: String,
        var expression: String,
        var result: String,
        var comment: String?,
        var locked: Boolean) : Serializable {

    override fun toString(): String {
        return "HistoryItem(id=$id, date='$date', expression='$expression', result='$result', comment='$comment', locked=$locked)"
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other?.javaClass != javaClass) return false

        other as HistoryItem

        if (id != other.id) return false
        if (date != other.date) return false
        if (expression != other.expression) return false
        if (result != other.result) return false
        if (comment != other.comment) return false
        if (locked != other.locked) return false

        return true
    }

    override fun hashCode(): Int {
        var result1 = id!!.hashCode()
        result1 = 31 * result1 + date.hashCode()
        result1 = 31 * result1 + expression.hashCode()
        result1 = 31 * result1 + result.hashCode()
        result1 = 31 * result1 + comment!!.hashCode()
        result1 = 31 * result1 + locked.hashCode()
        return result1
    }

}
