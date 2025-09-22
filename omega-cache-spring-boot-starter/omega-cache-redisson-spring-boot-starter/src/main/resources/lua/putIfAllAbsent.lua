--[[KEYS do not exist, create them and return true, otherwise return empty]]

for i, v in ipairs(KEYS) do
    if (redis.call('exists', v) == 1) then
        return nil;
    end
end
for i, v in ipairs(KEYS) do
    redis.call('set', v, 'default');
    redis.call('pexpire', v, ARGV[1]);
end
return true;
