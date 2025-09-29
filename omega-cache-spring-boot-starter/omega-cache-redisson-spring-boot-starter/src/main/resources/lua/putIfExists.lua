local key = KEYS[1]
local value = ARGV[1]
local expireNanos = tonumber(ARGV[2])

if redis.call('EXISTS', key) == 1 then
    redis.call('SET', key, value)
    if expireNanos > 0 then
        redis.call('PEXPIRE', key, expireNanos / 1000000)
    end
    return true
else
    return false
end
