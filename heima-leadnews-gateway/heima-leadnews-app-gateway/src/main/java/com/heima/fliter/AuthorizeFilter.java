package com.heima.fliter;


import com.alibaba.cloud.commons.lang.StringUtils;
import com.heima.until.AppJwtUtil;
import io.jsonwebtoken.Claims;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Component
@Slf4j
public class AuthorizeFilter implements Ordered, GlobalFilter {

    /*

     */
    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        //1.获取request response
        ServerHttpRequest request = exchange.getRequest();
        ServerHttpResponse response = exchange.getResponse();
        boolean contains = request.getURI().getPath().contains("/login");


        if (request.getURI().getPath().contains("/login")){
            return chain.filter(exchange);
        }
        //2.获取token
        //3.判断token时候存在
        String token = request.getHeaders().getFirst("token");
        if (StringUtils.isBlank(token)){
           response.setStatusCode(HttpStatus.UNAUTHORIZED);
           return response.setComplete();
       }

        //4.判断是否有效

        try {
            Claims claimsBody = AppJwtUtil.getClaimsBody(token);
            int result = AppJwtUtil.verifyToken(claimsBody);
            if(result == 1 || result == 2){
                response.setStatusCode(HttpStatus.UNAUTHORIZED);
                return response.setComplete();
            }

            //获取用户信息存入head
            Object userId = claimsBody.getId();
            //存储header中
            ServerHttpRequest serverHttpRequest = request.mutate().headers(httpHeader -> {
                httpHeader.add("userId", userId + "");
            }).build();

            //重置请求
            exchange.mutate().request(serverHttpRequest);
        } catch (Exception e) {
            e.printStackTrace();
            response.setStatusCode(HttpStatus.UNAUTHORIZED);
            return response.setComplete();
        }

        return chain.filter(exchange);
    }

    /*
    优先级设置 值越小越先执行
     */
    @Override
    public int getOrder() {
        return 0;
    }
}
