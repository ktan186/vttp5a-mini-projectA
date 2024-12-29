package sg.edu.nus.iss.vttp5a_mini_project_a.repository;

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import sg.edu.nus.iss.vttp5a_mini_project_a.util.Util;


@Repository
public class MapRepo {
    
    @Autowired
    @Qualifier(Util.template01)
    RedisTemplate<String, Object> template;

    public void putValue(String keyName, String mapKey, Object object) {
        template.opsForHash().put(keyName, mapKey, object);
    }

    public void deleteValue(String keyName, String mapKey) {
        template.opsForHash().delete(keyName, mapKey);
    }

    public Boolean hasValue(String keyName, String mapKey) {
        return template.opsForHash().hasKey(keyName, mapKey);
    }

    public Object getValue(String keyName, String mapKey) {
        return template.opsForHash().get(keyName, mapKey);
    }

    public Set<Object> getAllKeys(String keyName) {
        return template.opsForHash().keys(keyName);
    }

    public List<Object> getAllValues(String keyName) {
        return template.opsForHash().values(keyName);
    }

    public Map<Object, Object> getAllEntries(String keyName) {
        return template.opsForHash().entries(keyName);
    }

    public long mapSize(String keyName) {
        return template.opsForHash().size(keyName);
    }

    public void incrementValue(String keyName, String mapKey) {
        template.opsForHash().increment(keyName, mapKey, 1);
    }

}