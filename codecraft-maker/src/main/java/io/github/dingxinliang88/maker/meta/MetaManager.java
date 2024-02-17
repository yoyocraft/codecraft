package io.github.dingxinliang88.maker.meta;

import cn.hutool.core.io.resource.ResourceUtil;
import cn.hutool.json.JSONUtil;
import lombok.Getter;

/**
 * 元信息管理器
 *
 * @author <a href="https://github.com/dingxinliang88">youyi</a>
 */
public class MetaManager {

    private MetaManager() {
    }

    public static Meta getMeta() {
        return MetaHolder.INSTANCE.getMeta();
    }

    @Getter
    private enum MetaHolder {
        INSTANCE;

        private final Meta meta;

        MetaHolder() {
            meta = initMeta();
        }

        private Meta initMeta() {
            String metaJson = ResourceUtil.readUtf8Str("meta.json");
            Meta newMeta = JSONUtil.toBean(metaJson, Meta.class);
            MetaValidator.doValidateAndFill(newMeta);
            return newMeta;
        }
    }

}
