package com.ffforever.cartservice.service.impl;



import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ffforever.cartservice.domain.dto.CartFormDTO;
import com.ffforever.cartservice.domain.po.Cart;
import com.ffforever.cartservice.domain.vo.CartVO;
import com.ffforever.cartservice.mapper.CartMapper;
import com.ffforever.cartservice.service.ICartService;
import com.ffforever.hmapi.client.ItemClient;
import com.ffforever.hmapi.dto.ItemDTO;
import com.hmall.common.exception.BizIllegalException;
import com.hmall.common.utils.BeanUtils;
import com.hmall.common.utils.CollUtils;
import com.hmall.common.utils.UserContext;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

/**
 * <p>
 * 订单详情表 服务实现类
 * </p>
 *
 * @author 虎哥
 * @since 2023-05-05
 */
@Service
@RequiredArgsConstructor
public class CartServiceImpl extends ServiceImpl<CartMapper, Cart> implements ICartService {

    //private final IItemService itemService;
    //@Resource(name = "itemsWebClient")
    //WebClient webClient;

    @Resource
    private ItemClient itemClient;




    @Override
    public void addItem2Cart(CartFormDTO cartFormDTO) {
        // 1.获取登录用户
        Long userId = UserContext.getUser();

        // 2.判断是否已经存在
        if(checkItemExists(cartFormDTO.getItemId(), userId)){
            // 2.1.存在，则更新数量
            baseMapper.updateNum(cartFormDTO.getItemId(), userId);
            return;
        }
        // 2.2.不存在，判断是否超过购物车数量
        checkCartsFull(userId);

        // 3.新增购物车条目
        // 3.1.转换PO
        Cart cart = BeanUtils.copyBean(cartFormDTO, Cart.class);
        // 3.2.保存当前用户
        cart.setUserId(userId);
        // 3.3.保存到数据库
        save(cart);
    }

    @Override
    public List<CartVO> queryMyCarts() {
        // 1.查询我的购物车列表
        //todo 1L后续换为userId
        List<Cart> carts = lambdaQuery().eq(Cart::getUserId, 1L).list();
        if (CollUtils.isEmpty(carts)) {
            return CollUtils.emptyList();
        }

        // 2.转换VO
        List<CartVO> vos = BeanUtils.copyList(carts, CartVO.class);

        // 3.处理VO中的商品信息
        handleCartItems(vos);

        // 4.返回
        return vos;
    }

    /**
     * 远程查询商品信息，更新购物车中商品信息
     * @param vos
     */
    private void handleCartItems(List<CartVO> vos) {
        Set<Long> itemIds = vos.stream().map(CartVO::getItemId).collect(Collectors.toSet());
        // 2.查询商品
        List<ItemDTO> itemDTOS = itemClient.queryItemByIds(new LinkedList<>(itemIds));
        Map<Long, ItemDTO> itemMap = new HashMap<>();
        for(ItemDTO itemDTO : itemDTOS){
            Long id = itemDTO.getId();
            itemMap.put(id, itemDTO);
        }
        for (CartVO v : vos) {
            ItemDTO item = itemMap.get(v.getItemId());
            if (item == null) {
                continue;
            }
            v.setNewPrice(item.getPrice());
            v.setStatus(item.getStatus());
            v.setStock(item.getStock());
        }
    }


    /**
     * 使用WebClient进行远程调用查询商品
     * @param itemIds 商品ids
     * @return 返回商品列表
     */
//    private Mono<List<ItemDTO>>getItems(Set<Long> itemIds){
//            String idsParam = itemIds.stream()
//                    .map(String::valueOf)
//                    .collect(Collectors.joining(",")); // 拼接成逗号分隔的字符串
//
//            return webClient.get()
//                    .uri(uriBuilder -> uriBuilder.path("/items") // 只写相对路径
//                            .queryParam("ids", idsParam)
//                            .build())
//                    .retrieve()
//                    .bodyToMono(new ParameterizedTypeReference<List<ItemDTO>>() {}) // 解析为 List<ItemDTO>
//                    .onErrorResume(e -> {
//                        System.err.println("查询商品失败：" + e.getMessage());
//                        return Mono.just(List.of()); // 失败时返回空列表，防止流中断
//                    });
//
//    }

    @Override
    public void removeByItemIds(Collection<Long> itemIds) {
        // 1.构建删除条件，userId和itemId
        QueryWrapper<Cart> queryWrapper = new QueryWrapper<Cart>();
        queryWrapper.lambda()
                .eq(Cart::getUserId, UserContext.getUser())
                .in(Cart::getItemId, itemIds);
        // 2.删除
        remove(queryWrapper);
    }

    private void checkCartsFull(Long userId) {
        int count = Math.toIntExact(lambdaQuery().eq(Cart::getUserId, userId).count());
        if (count >= 10) {
            throw new BizIllegalException(StrUtil.format("用户购物车课程不能超过{}", 10));
        }
    }

    private boolean checkItemExists(Long itemId, Long userId) {
        int count = Math.toIntExact(lambdaQuery()
                .eq(Cart::getUserId, userId)
                .eq(Cart::getItemId, itemId)
                .count());
        return count > 0;
    }
}
