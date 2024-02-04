package io.github.dingxinliang88.maker.meta;

import cn.hutool.core.io.resource.ResourceUtil;
import cn.hutool.json.JSONUtil;

/**
 * 元信息管理器
 *
 * @author <a href="https://github.com/dingxinliang88">youyi</a>
 */
public class MetaManager {


    private static volatile Meta meta;

    private MetaManager() {
    }

    public static Meta getMeta() {
        if (meta == null) {
            synchronized (MetaManager.class) {
                if (meta == null) {
                    meta = initMeta();
                }
            }
        }
        return meta;
    }

    private static Meta initMeta() {
        String metaJson = ResourceUtil.readUtf8Str("meta.json");
//        String metaJson = ResourceUtil.readUtf8Str("springboot-init-meta.json");
        Meta newMeta = JSONUtil.toBean(metaJson, Meta.class);
        MetaValidator.doValidateAndFill(newMeta);
        return newMeta;
    }


}
