package io.github.dingxinliang88.maker.meta;

import org.junit.Test;

/**
 * @author <a href="https://github.com/dingxinliang88">youyi</a>
 */
public class MetaManagerTest {

    @Test
    public void getMeta() {
        Meta meta = MetaManager.getMeta();
        System.out.println(meta);
    }
}