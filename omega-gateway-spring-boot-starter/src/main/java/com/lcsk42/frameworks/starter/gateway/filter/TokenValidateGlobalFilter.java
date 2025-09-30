package com.lcsk42.frameworks.starter.gateway.filter;

import com.lcsk42.frameworks.starter.common.util.JwtUtil;
import com.lcsk42.frameworks.starter.core.constant.OrderedConstant;
import com.lcsk42.frameworks.starter.gateway.config.GatewayConfiguration;
import com.lcsk42.frameworks.starter.gateway.util.ServerUtil;
import lombok.RequiredArgsConstructor;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.PathContainer;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.util.pattern.PathPattern;
import org.springframework.web.util.pattern.PathPatternParser;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

@Order(OrderedConstant.Filter.TOKEN)
@RequiredArgsConstructor
public class TokenValidateGlobalFilter implements GlobalFilter {

    private final GatewayConfiguration configuration;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();

        if (isRequestAllowed(request, configuration.getAllowList())) {
            return chain.filter(exchange);
        }

        String authorization = request.getHeaders().getFirst(HttpHeaders.AUTHORIZATION);

        return Optional.ofNullable(authorization)
                .map(token -> JwtUtil.validateToken(token, configuration.getTokenSecret()))
                .map(userInfoDTO -> chain.filter(exchange))
                .orElseGet(() -> {
                    ServerHttpResponse response = exchange.getResponse();
                    String requestId = ServerUtil.getRequestId(request);
                    return ServerUtil.write(
                            response,
                            HttpStatus.UNAUTHORIZED,
                            HttpStatus.UNAUTHORIZED.getReasonPhrase(),
                            requestId);
                });
    }


    private boolean isRequestAllowed(ServerHttpRequest request,
            List<GatewayConfiguration.HttpEndpoint> allowList) {

        HttpMethod requestMethod = request.getMethod();
        String requestPath = request.getPath().toString();

        // 忽略文档相关接口
        if (Stream.of(
                "/",
                "/*/doc/**",
                "/*/v2/api-docs/**",
                "/*/v3/api-docs/**",
                "/*/webjars/**",
                "/*/swagger-resources/**",
                "/*/swagger-ui.html")
                .anyMatch(resourcePath -> isPathMatch(requestPath, resourcePath))) {
            return true;
        }

        // 白名单为空，直接返回 false
        if (CollectionUtils.isEmpty(allowList)) {
            return false;
        }

        // 是否在白名单中
        return allowList.stream()
                .anyMatch(endpoint -> isMethodMatch(requestMethod, endpoint.getMethod()) &&
                        isPathMatch(requestPath, endpoint.getPath()));
    }

    private static boolean isMethodMatch(HttpMethod requestMethod, String endpointMethod) {
        if (StringUtils.isBlank(endpointMethod)) {
            return true;
        }
        return requestMethod.name().equalsIgnoreCase(endpointMethod);
    }

    private static boolean isPathMatch(String requestPath, String endpointPath) {
        if (StringUtils.isAnyBlank(requestPath, endpointPath)) {
            return false;
        }
        PathPattern pattern = PathPatternParser.defaultInstance.parse(endpointPath);
        return pattern.matches(PathContainer.parsePath(requestPath));
    }
}
