package com.lcsk42.frameworks.starter.gateway.controller;

import com.lcsk42.frameworks.starter.core.constant.StringConstant;
import io.swagger.v3.oas.annotations.Hidden;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@Hidden
@RestController
public class DevToolsController {
    @GetMapping("/.well-known/appspecific/com.chrome.devtools.json")
    public String handleChromeDevTools() {
        return StringConstant.EMPTY_JSON;
    }
}
