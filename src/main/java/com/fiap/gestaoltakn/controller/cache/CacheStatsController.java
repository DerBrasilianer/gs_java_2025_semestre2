package com.fiap.gestaofrota.controller;

import com.github.benmanes.caffeine.cache.stats.CacheStats;
import org.springframework.cache.CacheManager;
import org.springframework.cache.caffeine.CaffeineCache;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/cache")
public class CacheStatsController {

    private final CacheManager cacheManager;

    public CacheStatsController(CacheManager cacheManager) {
        this.cacheManager = cacheManager;
    }

    @GetMapping("/stats")
    public Map<String, Object> getCacheStats() {
        Map<String, Object> stats = new HashMap<>();

        // Estatísticas do cache de departamentos
        CaffeineCache departamentosCache = (CaffeineCache) cacheManager.getCache("departamentos");
        if (departamentosCache != null) {
            CacheStats departamentosStats = departamentosCache.getNativeCache().stats();
            stats.put("departamentos", Map.of(
                    "hitCount", departamentosStats.hitCount(),
                    "missCount", departamentosStats.missCount(),
                    "loadSuccessCount", departamentosStats.loadSuccessCount(),
                    "loadFailureCount", departamentosStats.loadFailureCount(),
                    "totalLoadTime", departamentosStats.totalLoadTime(),
                    "evictionCount", departamentosStats.evictionCount(),
                    "hitRate", departamentosStats.hitRate(),
                    "missRate", departamentosStats.missRate()
            ));
        }

        // Estatísticas do cache de funcionários
        CaffeineCache funcionariosCache = (CaffeineCache) cacheManager.getCache("funcionarios");
        if (funcionariosCache != null) {
            CacheStats funcionariosStats = funcionariosCache.getNativeCache().stats();
            stats.put("funcionarios", Map.of(
                    "hitCount", funcionariosStats.hitCount(),
                    "missCount", funcionariosStats.missCount(),
                    "loadSuccessCount", funcionariosStats.loadSuccessCount(),
                    "loadFailureCount", funcionariosStats.loadFailureCount(),
                    "totalLoadTime", funcionariosStats.totalLoadTime(),
                    "evictionCount", funcionariosStats.evictionCount(),
                    "hitRate", funcionariosStats.hitRate(),
                    "missRate", funcionariosStats.missRate()
            ));
        }

        return stats;
    }

    @GetMapping("/clear")
    public String clearAllCaches() {
        cacheManager.getCacheNames().forEach(cacheName -> {
            cacheManager.getCache(cacheName).clear();
            System.out.println("🧹 Cache cleared: " + cacheName);
        });
        return "All caches cleared!";
    }

}
