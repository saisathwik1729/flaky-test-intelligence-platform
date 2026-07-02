package com.ftip.ftip.service;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.ftip.ftip.entity.TestRun;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.ArrayList;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class RedisService {
    private final RedisTemplate<String,Object>redisTemplate;
    private final ObjectMapper objectMapper=new ObjectMapper().registerModule(new JavaTimeModule());
    private static final String RUN_KEY_PREFIX="test:runs:";
    private static final int MAX_CACHED_RUNS=100;
    private static final long TTL_HOURS=24;

    public void cacheTestRuns(UUID testId, List<TestRun>runs)
    {
        String key=RUN_KEY_PREFIX+testId;
        redisTemplate.opsForValue().set(key,runs,TTL_HOURS,TimeUnit.HOURS);
    }
    public List<TestRun>getCachedTestRuns(UUID testId)
    {
        String key=RUN_KEY_PREFIX+testId;
        Object cached=redisTemplate.opsForValue().get(key);
        if(cached==null)
        {
            return null;
        }
        try{
            return objectMapper.convertValue(cached,new TypeReference<List<TestRun>>(){});
        }
        catch(Exception e)
        {
            redisTemplate.delete(key);
            return null;
        }
    }
    public void invalidateTestRuns(UUID testId)
    {
        redisTemplate.delete(RUN_KEY_PREFIX+testId);
    }

}
