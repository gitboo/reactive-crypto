/*
 * Copyright 2019 namjug-kim
 *
 * LINE Corporation licenses this file to you under the Apache License,
 * version 2.0 (the "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at:
 *
 *   https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations
 * under the License.
 */

package com.njkim.reactivecrypto.core.common.model.currency

data class CurrencyPair(
    val baseCurrency: Currency,
    val quoteCurrency: Currency
) {
    companion object {
        @JvmStatic
        fun parse(targetCurrency: String, baseCurrency: String): CurrencyPair {
            return CurrencyPair(
                Currency.getInstance(targetCurrency.toUpperCase()),
                Currency.getInstance(baseCurrency.toUpperCase())
            )
        }

        @JvmStatic
        fun parse(toStringValue: String): CurrencyPair {
            val split = toStringValue.split("-")
            return parse(split[0], split[1])
        }
    }

    override fun toString(): String {
        return "$baseCurrency-$quoteCurrency"
    }
}
