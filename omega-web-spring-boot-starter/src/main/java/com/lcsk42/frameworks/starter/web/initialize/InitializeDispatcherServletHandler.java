package com.lcsk42.frameworks.starter.web.initialize;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.http.HttpMethod;
import org.springframework.web.client.RestTemplate;

import static com.lcsk42.frameworks.starter.web.config.WebAutoConfiguration.INITIALIZE_PATH;

@RequiredArgsConstructor
public final class InitializeDispatcherServletHandler implements CommandLineRunner {

    // Http rest 请求模版
    private final RestTemplate restTemplate;

    // 用于获取服务器端口
    private final PortHolder portHolder;

    // 环境变量
    private final ConfigurableEnvironment configurableEnvironment;

    /**
     * 该方法在应用启动时执行。通过触发一个 GET 请求来初始化 DispatcherServlet，以提升首个请求的响应速度。
     */
    @Override
    public void run(String... args) throws Exception {
        // 构建用于调用初始化端点的 URL
        String url = String.format("http://127.0.0.1:%s%s",
                portHolder.getPort()
                        + configurableEnvironment.getProperty("server.servlet.context-path", ""),
                INITIALIZE_PATH);

        try {
            // 发送 GET 请求
            restTemplate.execute(url, HttpMethod.GET, null, null);
        } catch (Throwable ignored) {
            // 忽略异常
        }
    }
}
