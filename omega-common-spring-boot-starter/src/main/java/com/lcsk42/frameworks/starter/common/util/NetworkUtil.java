package com.lcsk42.frameworks.starter.common.util;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.SneakyThrows;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.Enumeration;

/**
 * 网络工具类
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class NetworkUtil {

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
}
