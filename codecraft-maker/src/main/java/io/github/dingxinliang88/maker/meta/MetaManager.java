package io.github.dingxinliang88.maker.meta;

import cn.hutool.core.io.resource.ResourceUtil;
import cn.hutool.json.JSONUtil;
import java.util.concurrent.atomic.AtomicReference;

/**
 * 元信息管理器
 *
 * @author <a href="https://github.com/dingxinliang88">youyi</a>
 */
public class MetaManager {


    public static Meta getMeta() {
        return MetaHolder.getMeta();
    }

    private static class MetaHolder {

        private static final AtomicReference<Meta> META_REF = new AtomicReference<>();

        private static Meta initMeta() {
            String metaJson = ResourceUtil.readUtf8Str("meta.json");
            Meta newMeta = JSONUtil.toBean(metaJson, Meta.class);
            MetaValidator.doValidateAndFill(newMeta);
            return newMeta;
        }

        private static Meta getMeta() {
            Meta meta = META_REF.get();
            if (meta == null) {
                synchronized (MetaHolder.class) {
                    if (META_REF.get() == null) {
                        meta = initMeta();
                        META_REF.set(meta);
                    }
                }
            }
            return meta;
        }
    }

}
