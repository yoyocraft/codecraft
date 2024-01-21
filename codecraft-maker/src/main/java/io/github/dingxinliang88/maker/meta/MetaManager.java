package io.github.dingxinliang88.maker.meta;

import cn.hutool.core.io.resource.ResourceUtil;
import cn.hutool.json.JSONUtil;
import io.github.dingxinliang88.maker.meta.Meta.FileConfig;

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
        Meta newMeta = JSONUtil.toBean(metaJson, Meta.class);
        Meta.FileConfig fileConfig = newMeta.getFileConfig();
        // TODO 校验和处理默认值
        return newMeta;
    }


}
