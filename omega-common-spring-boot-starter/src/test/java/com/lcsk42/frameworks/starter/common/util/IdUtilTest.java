package com.lcsk42.frameworks.starter.common.util;

import com.lcsk42.frameworks.starter.common.snowflake.Snowflake;
import com.lcsk42.frameworks.starter.core.Singleton;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class IdUtilTest {

    private MockedStatic<InetAddress> mockedInetAddress;
    private MockedStatic<NetworkInterface> mockedNetworkInterface;
    private MockedStatic<Singleton> mockedSingleton;
    private MockedStatic<ThreadLocalRandom> mockedThreadLocalRandom;
    private MockedStatic<UUID> mockedUUID;

    @BeforeEach
    void setUp() {
        mockedSingleton = mockStatic(Singleton.class);
        mockedThreadLocalRandom = mockStatic(ThreadLocalRandom.class);
        mockedUUID = mockStatic(UUID.class);
    }

    @AfterEach
    void tearDown() {
        if (mockedInetAddress != null) {
            mockedInetAddress.close();
        }
        if (mockedNetworkInterface != null) {
            mockedNetworkInterface.close();
        }
        mockedSingleton.close();
        mockedThreadLocalRandom.close();
        mockedUUID.close();
    }

    @Test
    void testGenerateStandardUuid() {
        UUID mockUuid = mock(UUID.class);
        when(mockUuid.toString()).thenReturn("550e8400-e29b-41d4-a716-446655440000");
        when(UUID.randomUUID()).thenReturn(mockUuid);

        String result = IdUtil.generateStandardUuid();
        assertEquals("550e8400-e29b-41d4-a716-446655440000", result);
    }

    @Test
    void testGenerateCompactUuid() {
        UUID mockUuid = mock(UUID.class);
        when(mockUuid.toString()).thenReturn("550e8400-e29b-41d4-a716-446655440000");
        when(UUID.randomUUID()).thenReturn(mockUuid);

        String result = IdUtil.generateCompactUuid();
        assertEquals("550e8400e29b41d4a716446655440000", result);
    }

    @Test
    void testGenerateWorkerIdWithValidHostInfo() throws Exception {
        InetAddress mockInetAddress = mock(InetAddress.class);
        when(mockInetAddress.getHostName()).thenReturn("test-host");

        NetworkInterface mockNetworkInterface = mock(NetworkInterface.class);
        byte[] mac = {0x12, 0x34, 0x56, 0x78, (byte) 0x9A, (byte) 0xBC};
        when(mockNetworkInterface.getHardwareAddress()).thenReturn(mac);

        mockedInetAddress = mockStatic(InetAddress.class);
        mockedInetAddress.when(InetAddress::getLocalHost).thenReturn(mockInetAddress);

        mockedNetworkInterface = mockStatic(NetworkInterface.class);
        mockedNetworkInterface.when(() -> NetworkInterface.getByInetAddress(mockInetAddress))
                .thenReturn(mockNetworkInterface);

        long workerBits = 5;
        long result = IdUtil.generateWorkerId(workerBits);
        long maxWorkerId = ~(-1L << workerBits);

        assertTrue(result >= 0 && result <= maxWorkerId);
    }

    @Test
    void testGenerateWorkerIdWithInvalidHostInfo() throws Exception {
        mockedInetAddress = mockStatic(InetAddress.class);
        mockedInetAddress.when(InetAddress::getLocalHost).thenThrow(new RuntimeException("Network error"));

        long workerBits = 5;
        long maxWorkerId = ~(-1L << workerBits);
        long expectedRandom = 3L;

        ThreadLocalRandom mockRandom = mock(ThreadLocalRandom.class);
        when(mockRandom.nextLong(0, maxWorkerId + 1)).thenReturn(expectedRandom);
        mockedThreadLocalRandom.when(ThreadLocalRandom::current).thenReturn(mockRandom);

        long result = IdUtil.generateWorkerId(workerBits);
        assertEquals(expectedRandom, result);
    }

    @Test
    void testGenerateDatacenterIdWithValidIp() throws Exception {
        InetAddress mockInetAddress = mock(InetAddress.class);
        when(mockInetAddress.getHostAddress()).thenReturn("192.168.1.1");

        mockedInetAddress = mockStatic(InetAddress.class);
        mockedInetAddress.when(InetAddress::getLocalHost).thenReturn(mockInetAddress);

        long datacenterBits = 5;
        long result = IdUtil.generateDatacenterId(datacenterBits);
        long maxDatacenterId = ~(-1L << datacenterBits);

        assertTrue(result >= 0 && result <= maxDatacenterId);
    }

    @Test
    void testGenerateDatacenterIdWithInvalidIp() throws Exception {
        mockedInetAddress = mockStatic(InetAddress.class);
        mockedInetAddress.when(InetAddress::getLocalHost).thenThrow(new RuntimeException("Network error"));

        long datacenterBits = 5;
        long maxDatacenterId = ~(-1L << datacenterBits);
        long expectedRandom = 2L;

        ThreadLocalRandom mockRandom = mock(ThreadLocalRandom.class);
        when(mockRandom.nextLong(0, maxDatacenterId + 1)).thenReturn(expectedRandom);
        mockedThreadLocalRandom.when(ThreadLocalRandom::current).thenReturn(mockRandom);

        long result = IdUtil.generateDatacenterId(datacenterBits);
        assertEquals(expectedRandom, result);
    }

    @Test
    void testGetSnowflakeWhenNotInSingleton() {
        when(Singleton.get(Snowflake.class.getName())).thenReturn(null);

        Snowflake result = IdUtil.getSnowflake();
        assertNotNull(result);
        mockedSingleton.verify(() -> Singleton.put(result), times(1));
    }

    @Test
    void testGetSnowflakeWhenInSingleton() {
        Snowflake mockSnowflake = mock(Snowflake.class);
        when(Singleton.get(Snowflake.class.getName())).thenReturn(mockSnowflake);

        Snowflake result = IdUtil.getSnowflake();
        assertEquals(mockSnowflake, result);
        mockedSingleton.verify(() -> Singleton.put(any()), never());
    }

    @Test
    void testGetSnowflakeNextId() {
        Snowflake mockSnowflake = mock(Snowflake.class);
        when(Singleton.get(Snowflake.class.getName())).thenReturn(mockSnowflake);
        when(mockSnowflake.nextId()).thenReturn(12345L);

        long result = IdUtil.getSnowflakeNextId();
        assertEquals(12345L, result);
    }

    @Test
    void testGetSnowflakeNextIdString() {
        Snowflake mockSnowflake = mock(Snowflake.class);
        when(Singleton.get(Snowflake.class.getName())).thenReturn(mockSnowflake);
        when(mockSnowflake.nextIdString()).thenReturn("12345");

        String result = IdUtil.getSnowflakeNextIdString();
        assertEquals("12345", result);
    }
}
