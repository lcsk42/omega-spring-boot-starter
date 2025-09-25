package com.lcsk42.frameworks.starter.common.util;

import com.lcsk42.frameworks.starter.core.ApplicationContextHolder;
import com.lcsk42.frameworks.starter.core.constant.StringConstant;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import net.dreamlu.mica.ip2region.core.Ip2regionSearcher;
import net.dreamlu.mica.ip2region.core.IpInfo;
import org.apache.commons.text.StringEscapeUtils;

import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class IpUtil {
    /**
     * 查询 IP 归属地（本地库解析）
     *
     * @param ip IP 地址
     * @return IP 归属地
     */
    public static String getIpv4Address(String ip) {
        if (isInnerIpv4(ip)) {
            return "内网IP";
        }

        Ip2regionSearcher ip2regionSearcher = ApplicationContextHolder.getBean(Ip2regionSearcher.class);
        IpInfo ipInfo = ip2regionSearcher.memorySearch(ip);
        if (ipInfo == null) {
            return null;
        }

        return Set.of(
                        ipInfo.getCountry(),
                        ipInfo.getRegion(),
                        ipInfo.getProvince(),
                        ipInfo.getCity(),
                        ipInfo.getIsp()
                ).stream()
                .filter(Objects::nonNull)
                .collect(Collectors.joining(StringConstant.PIPE));
    }

    /**
     * 是否为内网 IPv4
     *
     * @param ip IP 地址
     * @return 是否为内网 IP
     */
    public static boolean isInnerIpv4(String ip) {
        return NetworkUtil.isInnerIP(
                "0:0:0:0:0:0:0:1".equals(ip) ?
                        "127.0.0.1" :
                        StringEscapeUtils.escapeHtml4(ip)
        );
    }
}
