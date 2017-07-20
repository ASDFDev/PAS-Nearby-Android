package org.sp.attendance.utils;

import com.vincentbrison.openlibraries.android.dualcache.Builder;
import com.vincentbrison.openlibraries.android.dualcache.CacheSerializer;
import com.vincentbrison.openlibraries.android.dualcache.DualCache;
import com.vincentbrison.openlibraries.android.dualcache.JsonSerializer;

import java.util.Date;

public class CacheManager {

    private DualCache<Date> cache;

    private void initCache(){
        cache.invalidateRAM();
        String cacheId = "timeStamp";
        CacheSerializer<Date> jsonSerializer = new JsonSerializer<>(Date.class);
        int ramCacheSize = 40;
        cache = new Builder<Date>(cacheId, 1)
                .enableLog()
                .noDisk()
                .useSerializerInRam(ramCacheSize, jsonSerializer)
                .build();
    }

    public void storeTimeStampCache(Date timeStamp){
        initCache();
        cache.put("timeStamp", timeStamp);
    }

    public Date getTimeStampCache(){
        return cache.get("timeStamp");
    }

}
