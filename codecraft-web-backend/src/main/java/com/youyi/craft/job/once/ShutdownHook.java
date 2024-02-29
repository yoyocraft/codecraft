package com.youyi.craft.job.once;

import cn.hutool.core.io.FileUtil;
import com.youyi.craft.manager.LocalFileCacheManager;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.stereotype.Component;

/**
 * 清理本地缓存
 *
 * @author <a href="https://github.com/dingxinliang88">youyi</a>
 */
@Slf4j
@Component
public class ShutdownHook implements DisposableBean {

    @Override
    public void destroy() throws Exception {
        // 清理本地缓存
        LocalFileCacheManager.clearAllCache();
        // 清理文件
        String projectPath = System.getProperty("user.dir");
        String localFilePath = projectPath + "/.tmp/cache";
        FileUtil.del(localFilePath);
        log.info("clear local file cache finished!");
    }
}
