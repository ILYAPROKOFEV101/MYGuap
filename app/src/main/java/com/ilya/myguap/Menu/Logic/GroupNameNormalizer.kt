package com.ilya.myguap.Menu.Logic

object GroupNameNormalizer {
    fun normalize(input: String): String {
        // Извлекаем только цифры из строки
        val digits = input.filter { it.isDigit() }
        // Если строка содержит только цифры, добавляем префикс "С"
        return if (digits.isNotEmpty()) {
            "С$digits" // Добавляем префикс "С"
        } else {
            "" // Если цифр нет, возвращаем пустую строку
        }
    }
}