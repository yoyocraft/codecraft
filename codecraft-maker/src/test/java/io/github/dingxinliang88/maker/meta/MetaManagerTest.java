package io.github.dingxinliang88.maker.meta;

import lombok.extern.slf4j.Slf4j;
import org.junit.Test;

/**
 * @author <a href="https://github.com/dingxinliang88">youyi</a>
 */
@Slf4j
public class MetaManagerTest {

    @Test
    public void getMeta() {
        Meta meta = MetaManager.getMeta();
        log.info("{}", meta);
    }
}