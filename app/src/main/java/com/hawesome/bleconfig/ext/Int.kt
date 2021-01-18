package com.hawesome.bleconfig.ext

import kotlin.math.pow

fun Int.pow(n: Int) = this.toDouble().pow(n).toInt()