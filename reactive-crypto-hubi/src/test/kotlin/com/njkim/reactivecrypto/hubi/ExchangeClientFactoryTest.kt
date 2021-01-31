package com.njkim.reactivecrypto.hubi

import com.njkim.reactivecrypto.core.ExchangeClientFactory
import com.njkim.reactivecrypto.core.websocket.ExchangePublicWebsocketClient
import com.njkim.reactivecrypto.core.common.model.ExchangeVendor
import org.assertj.core.api.Assertions.assertThat
import org.junit.Test

class ExchangeClientFactoryTest {
    @Test
    fun `create hubi websocket client`() {
        val exchangeWebsocketClient = ExchangeClientFactory.publicWebsocket(ExchangeVendor.HUBI)

        assertThat(exchangeWebsocketClient).isNotNull
        assertThat(exchangeWebsocketClient).isInstanceOf(ExchangePublicWebsocketClient::class.java)
        assertThat(exchangeWebsocketClient).isExactlyInstanceOf(HubiWebsocketClient::class.java)
    }
}
