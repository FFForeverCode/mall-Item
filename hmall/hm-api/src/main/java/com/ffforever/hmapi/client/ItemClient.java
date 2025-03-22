package com.ffforever.hmapi.client;

import com.ffforever.hmapi.dto.ItemDTO;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

//item-service的服务端
@FeignClient("item-service")
public interface ItemClient {
    //根据服务名称到治理中心找到对应的服务端，调用对应的controller方法，获取结果
    @GetMapping("/items")
    public List<ItemDTO> queryItemByIds(@RequestParam("ids") List<Long> ids);
}
