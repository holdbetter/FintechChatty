package com.holdbetter.fintechchatproject.domain.retrofit

sealed class Narrow(vararg operators: Pair<Operator, Operand>) {
    private val mapOperators = operators.associate { it }

    fun toJson(): String {
        return mapOperators.map {
            when (it.value) {
                is Operand.IntOperand -> "{\"operator\":\"${it.key.value}\", \"operand\":${(it.value as Operand.IntOperand).operand}}"
                is Operand.StringOperand -> "{\"operator\":\"${it.key.value}\", \"operand\":\"${(it.value as Operand.StringOperand).operand}\"}"
            }
        }.joinToString(prefix = "[ ", postfix = " ]")
    }

    @JvmInline
    value class Operator(val value: String)

    class MessageNarrow(streamId: Long, topicName: String) : Narrow(
        Operator("stream") to Operand.IntOperand(streamId),
        Operator("topic") to Operand.StringOperand(topicName)
    )
}

sealed class Operand {
    class StringOperand(val operand: String) : Operand()
    class IntOperand(val operand: Long) : Operand()
}