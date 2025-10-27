package com.lcsk42.frameworks.starter.common.util.net;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.SneakyThrows;
import org.apache.commons.lang3.StringUtils;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 网络工具类
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class NetworkUtil {

    private static final Pattern IPV4 = Pattern.compile(
            "^(25[0-5]|2[0-4]\\d|[01]?\\d?\\d)\\.(25[0-5]|2[0-4]\\d|[01]?\\d?\\d)\\.(25[0-5]|2[0-4]\\d|[01]?\\d?\\d)\\.(25[0-5]|2[0-4]\\d|[01]?\\d?\\d)$");

    public static final String LOCAL_IP = "127.0.0.1";

    /**
     * 查找第一个满足条件的网卡地址（非回路、非局域网、IPv4地址）， 如果没有满足要求的地址，则调用 {@link InetAddress#getLocalHost()} 获取地址
     *
     * @return 第一个符合条件的网卡地址，如果没有则返回本地主机地址
     */
    @SneakyThrows
    public static InetAddress getLocalhost() {
        Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();
        while (interfaces.hasMoreElements()) {
            NetworkInterface iface = interfaces.nextElement();
            // 跳过虚拟接口和未启用的接口
            if (iface.isVirtual() || !iface.isUp()) {
                continue;
            }

            Enumeration<InetAddress> addresses = iface.getInetAddresses();
            while (addresses.hasMoreElements()) {
                InetAddress addr = addresses.nextElement();
                // 检查是否为IPv4地址、非回路地址、非局域网地址
                if (addr instanceof Inet4Address && !addr.isLoopbackAddress()
                        && !addr.isSiteLocalAddress()) {
                    // 找到第一个符合条件的地址立即返回
                    return addr;
                }
            }
        }

        // 如果没有找到符合条件的地址，则回退到本地主机地址
        return InetAddress.getLocalHost();
    }

    /**
     * 从多级反向代理中获得第一个非unknown IP地址
     *
     * @param ip 获得的IP地址
     * @return 第一个非unknown IP地址
     * @since 4.4.1
     */
    public static String getMultistageReverseProxyIp(String ip) {
        // 多级反向代理检测
        if (ip != null && StringUtils.indexOf(ip, ',') > 0) {
            final List<String> ips = Arrays.stream(StringUtils.split(ip, ','))
                    .map(String::trim)
                    .toList();
            for (final String subIp : ips) {
                if (!isUnknown(subIp)) {
                    ip = subIp;
                    break;
                }
            }
        }
        return ip;
    }

    /**
     * 检测给定字符串是否为未知，多用于检测HTTP请求相关<br>
     *
     * @param checkString 被检测的字符串
     * @return 是否未知
     */
    public static boolean isUnknown(String checkString) {
        return StringUtils.isBlank(checkString) || "unknown".equalsIgnoreCase(checkString);
    }

    /**
     * 判定是否为内网IPv4<br>
     * 私有IP：
     *
     * <pre>
     * A类 10.0.0.0-10.255.255.255
     * B类 172.16.0.0-172.31.255.255
     * C类 192.168.0.0-192.168.255.255
     * </pre>
     * <p>
     * 当然，还有127这个网段是环回地址
     *
     * @param ipAddress IP地址
     * @return 是否为内网IP
     */
    public static boolean isInnerIP(String ipAddress) {
        boolean isInnerIp;
        long ipNum = ipv4ToLong(ipAddress);

        long aBegin = ipv4ToLong("10.0.0.0");
        long aEnd = ipv4ToLong("10.255.255.255");

        long bBegin = ipv4ToLong("172.16.0.0");
        long bEnd = ipv4ToLong("172.31.255.255");

        long cBegin = ipv4ToLong("192.168.0.0");
        long cEnd = ipv4ToLong("192.168.255.255");

        isInnerIp = isInner(ipNum, aBegin, aEnd) || isInner(ipNum, bBegin, bEnd)
                || isInner(ipNum, cBegin, cEnd) || LOCAL_IP.equals(ipAddress);
        return isInnerIp;
    }

    /**
     * 根据ip地址(xxx.xxx.xxx.xxx)计算出long型的数据
     * 方法别名：inet_aton
     *
     * @param strIP IP V4 地址
     * @return long值
     */
    public static long ipv4ToLong(String strIP) {
        final Matcher matcher = IPV4.matcher(strIP);
        if (matcher.matches()) {
            return matchAddress(matcher);
        }
        throw new IllegalArgumentException("Invalid IPv4 address!");
    }

    /**
     * 将匹配到的Ipv4地址的4个分组分别处理
     *
     * @param matcher 匹配到的Ipv4正则
     * @return ipv4对应long
     */
    private static long matchAddress(Matcher matcher) {
        long addr = 0;
        for (int i = 1; i <= 4; ++i) {
            addr |= Long.parseLong(matcher.group(i)) << 8 * (4 - i);
        }
        return addr;
    }

    /**
     * 指定IP的long是否在指定范围内
     *
     * @param userIp 用户IP
     * @param begin 开始IP
     * @param end 结束IP
     * @return 是否在范围内
     */
    private static boolean isInner(long userIp, long begin, long end) {
        return (userIp >= begin) && (userIp <= end);
    }
}
