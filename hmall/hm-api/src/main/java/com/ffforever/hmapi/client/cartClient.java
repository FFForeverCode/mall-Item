package com.ffforever.hmapi.client;

import org.springframework.cloud.openfeign.FeignClient;

@FeignClient("cart-service")
public interface cartClient {
}
