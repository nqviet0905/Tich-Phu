package com.example.personalfinance.data.model

import java.net.URLDecoder
import java.nio.charset.StandardCharsets

data class QrPaymentData(
    val merchantName: String,
    val bankCode: String,
    val accountNumber: String,
    val amount: Double? = null,
    val rawValue: String
)


object QrPaymentParser {

    private const val PREFIX =
        "TICHPHU_PAY"


    fun parse(
        rawValue: String
    ): QrPaymentData? {

        val raw =
            rawValue.trim()

        if (!raw.startsWith(PREFIX)) {
            return null
        }


        val parts =
            raw.split("|")

        if (parts.isEmpty()) {
            return null
        }


        val values =
            mutableMapOf<String, String>()


        parts
            .drop(1)
            .forEach { part ->

                val pair =
                    part.split(
                        "=",
                        limit = 2
                    )

                if (pair.size == 2) {

                    val key =
                        pair[0]
                            .trim()
                            .lowercase()

                    val value =
                        decode(
                            pair[1].trim()
                        )

                    values[key] =
                        value
                }
            }


        val merchant =
            values["merchant"]
                ?.trim()
                .orEmpty()

        val bank =
            values["bank"]
                ?.trim()
                .orEmpty()

        val account =
            values["account"]
                ?.replace(" ", "")
                ?.trim()
                .orEmpty()


        if (
            merchant.isBlank() ||
            bank.isBlank() ||
            account.isBlank()
        ) {

            return null
        }


        val amount =
            values["amount"]
                ?.replace(",", "")
                ?.replace(".", "")
                ?.toDoubleOrNull()


        return QrPaymentData(
            merchantName =
                merchant,

            bankCode =
                bank.uppercase(),

            accountNumber =
                account,

            amount =
                amount,

            rawValue =
                raw
        )
    }


    private fun decode(
        value: String
    ): String {

        return try {

            URLDecoder.decode(
                value,
                StandardCharsets.UTF_8.name()
            )

        } catch (_: Exception) {

            value
        }
    }
}