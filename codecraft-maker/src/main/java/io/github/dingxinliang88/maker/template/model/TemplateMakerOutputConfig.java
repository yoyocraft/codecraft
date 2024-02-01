package io.github.dingxinliang88.maker.template.model;

import lombok.Data;

/**
 * 模板制作输出规则
 *
 * @author <a href="https://github.com/dingxinliang88">youyi</a>
 */
@Data
public class TemplateMakerOutputConfig {

    /**
     * 从未分组文件中移除组内同名的文件
     */
    private boolean removeGroupFilesFromRoot = true;

}
