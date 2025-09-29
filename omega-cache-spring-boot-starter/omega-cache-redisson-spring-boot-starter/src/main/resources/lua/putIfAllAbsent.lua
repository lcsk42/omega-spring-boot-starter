local keys = KEYS
local expireNanos = tonumber(ARGV[1])

-- 检查所有key是否都不存在
for _, key in ipairs(keys) do
    if redis.call('EXISTS', key) == 1 then
        return false
    end
end

-- 设置所有key
for _, key in ipairs(keys) do
    redis.call('SET', key, '')
    if expireNanos > 0 then
        redis.call('PEXPIRE', key, expireNanos / 1000000)
    end
end

return true
