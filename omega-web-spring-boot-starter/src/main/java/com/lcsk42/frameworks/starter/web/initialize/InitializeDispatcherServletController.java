package com.lcsk42.frameworks.starter.web.initialize;

import io.swagger.v3.oas.annotations.Hidden;
import io.swagger.v3.oas.annotations.Operation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import static com.lcsk42.frameworks.starter.web.config.WebAutoConfiguration.INITIALIZE_PATH;

@Hidden
@Slf4j(topic = "Initialize DispatcherServlet")
@RestController
public final class InitializeDispatcherServletController {

    /**
     * 初始化 DispatcherServlet 的端点。 通过提前初始化该Servlet来优化首次响应时间。
     */
    @GetMapping(INITIALIZE_PATH)
    @Operation(summary = "Initialize DispatcherServlet",
            description = "该端点用于初始化 DispatcherServlet，以提升接口的首次响应速度")
    public void initializeDispatcherServlet() {
        // Logs the initialization of the DispatcherServlet.
        log.info(
                "[Omega] -Initialized the dispatcherServlet to improve the first response time of the interface...");
    }
}
