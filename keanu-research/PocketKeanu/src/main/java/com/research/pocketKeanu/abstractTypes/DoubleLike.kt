package com.research.pocketKeanu.abstractTypes

public interface DoubleLike<T : DoubleLike<T>> : ArithmeticOperators<T> {
    operator fun minus(value: Double): T
    operator fun plus(value: Double): T
    operator fun times(value: Double): T
    operator fun div(value: Double): T
    operator fun unaryMinus(): T

}