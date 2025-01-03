package Engine3d.Rendering.ResourceManager;

import java.util.HashMap;

public abstract class ResourceManager<T> {
    private final HashMap<String, T> resourceMap;

    public ResourceManager() {
        this.resourceMap = new HashMap<>();
    }

    public T getResource(String filepath) {
        if (filepath == null || filepath.equals("")) {return null;}
        if (resourceMap.containsKey(filepath))
        {
            return resourceMap.get(filepath);
        } else {
            if (loadResource(filepath) == null) {return null;}
            T resource = loadResource(filepath);
            resourceMap.put(filepath, resource);
            return resource;
        }
    }

    protected abstract T loadResource(String filepath);
}
