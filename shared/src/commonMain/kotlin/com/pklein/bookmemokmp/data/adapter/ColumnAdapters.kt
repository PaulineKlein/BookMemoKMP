package com.pklein.bookmemokmp.data.adapter

import app.cash.sqldelight.ColumnAdapter

val intListAdapter: ColumnAdapter<List<Int>, String> =
    object : ColumnAdapter<List<Int>, String> {
        override fun decode(databaseValue: String): List<Int> =
            if (databaseValue.isBlank()) {
                emptyList()
            } else {
                databaseValue.split(",").mapNotNull { it.trim().toIntOrNull() }
            }

        override fun encode(value: List<Int>): String = value.joinToString(",")
    }
