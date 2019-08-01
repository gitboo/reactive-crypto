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

package com.njkim.reactivecrypto.binance.http.raw

import com.fasterxml.jackson.module.kotlin.convertValue
import com.njkim.reactivecrypto.binance.BinanceJsonObjectMapper
import com.njkim.reactivecrypto.binance.model.BinanceOrderCancelResponse
import com.njkim.reactivecrypto.binance.model.BinancePlaceOrderResponse
import com.njkim.reactivecrypto.core.common.model.currency.CurrencyPair
import com.njkim.reactivecrypto.core.common.model.order.OrderType
import com.njkim.reactivecrypto.core.common.model.order.TimeInForceType
import com.njkim.reactivecrypto.core.common.model.order.TradeSideType
import com.njkim.reactivecrypto.core.common.util.toMultiValueMap
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.bodyToMono
import reactor.core.publisher.Mono
import java.math.BigDecimal
import java.time.Instant

class BinanceRawTradeOperator internal constructor(private val webClient: WebClient) {

    /**
     * Cancel an active order.
     * Either orderId or origClientOrderId must be sent.
     *
     * Weight: 1
     *
     * @param newClientOrderId Used to uniquely identify this cancel. Automatically generated by default.
     */
    fun cancelOrder(
        symbol: CurrencyPair,
        orderId: Long? = null,
        origClientOrderId: String? = null,
        newClientOrderId: String? = null,
        recvWindow: Long = 5000
    ): Mono<BinanceOrderCancelResponse> {
        val request = mapOf(
            "symbol" to symbol,
            "recvWindow" to recvWindow,
            "timestamp" to Instant.now().toEpochMilli()
        ).toMutableMap()
        orderId?.let { request["orderId"] = orderId }
        origClientOrderId?.let { request["origClientOrderId"] = origClientOrderId }
        newClientOrderId?.let { request["newClientOrderId"] = newClientOrderId }

        val convertedRequest = BinanceJsonObjectMapper.instance.convertValue<Map<String, Any>>(request)
            .toMultiValueMap()

        return webClient.delete()
            .uri {
                it.path("/api/v3/order")
                    .queryParams(convertedRequest)
                    .build()
            }
            .retrieve()
            .binanceErrorHandling()
            .bodyToMono()
    }

    fun limitOrder(
        symbol: CurrencyPair,
        side: TradeSideType,
        quantity: BigDecimal,
        timeInForce: TimeInForceType,
        price: BigDecimal,
        newClientOrderId: String? = null,
        recvWindow: Long = 5000
    ): Mono<BinancePlaceOrderResponse> {
        return placeOrder(
            symbol,
            side,
            OrderType.LIMIT,
            quantity,
            timeInForce = timeInForce,
            price = price,
            newClientOrderId = newClientOrderId,
            recvWindow = recvWindow
        )
    }

    fun marketOrder(
        symbol: CurrencyPair,
        side: TradeSideType,
        quantity: BigDecimal,
        newClientOrderId: String? = null,
        recvWindow: Long = 5000
    ): Mono<BinancePlaceOrderResponse> {
        return placeOrder(
            symbol,
            side,
            OrderType.MARKET,
            quantity,
            newClientOrderId = newClientOrderId,
            recvWindow = recvWindow
        )
    }

    /**
     * @param icebergQty Used with LIMIT, STOP_LOSS_LIMIT, and TAKE_PROFIT_LIMIT to create an iceberg order.
     * @param stopPrice Used with STOP_LOSS, STOP_LOSS_LIMIT, TAKE_PROFIT, and TAKE_PROFIT_LIMIT orders.
     */
    fun placeOrder(
        symbol: CurrencyPair,
        side: TradeSideType,
        type: OrderType,
        quantity: BigDecimal,
        icebergQty: BigDecimal? = null,
        timeInForce: TimeInForceType? = null,
        price: BigDecimal? = null,
        stopPrice: BigDecimal? = null,
        newClientOrderId: String? = null,
        recvWindow: Long = 5000
    ): Mono<BinancePlaceOrderResponse> {
        val request = mapOf(
            "symbol" to symbol,
            "side" to side,
            "type" to type,
            "quantity" to quantity,
            "recvWindow" to recvWindow,
            "timestamp" to Instant.now().toEpochMilli()
        ).toMutableMap()
        price?.let { request["price"] = price }
        stopPrice?.let { request["stopPrice"] = stopPrice }
        timeInForce?.let { request["timeInForce"] = timeInForce }
        icebergQty?.let { request["icebergQty"] = icebergQty }
        newClientOrderId?.let { request["newClientOrderId"] = newClientOrderId }

        val convertedRequest = BinanceJsonObjectMapper.instance.convertValue<Map<String, Any>>(request)
            .toMultiValueMap()

        return webClient.post()
            .uri {
                it.path("/api/v3/order")
                    .queryParams(convertedRequest)
                    .build()
            }
            .retrieve()
            .binanceErrorHandling()
            .bodyToMono()
    }
}
