package io.github.dingxinliang88.maker.generator;

import cn.hutool.core.io.FileUtil;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.attribute.PosixFilePermission;
import java.nio.file.attribute.PosixFilePermissions;
import java.util.Set;

/**
 * 脚本生成器
 *
 * @author <a href="https://github.com/dingxinliang88">youyi</a>
 */
public class ScriptGenerator {

    public static void doGenerate(final String jarPath, String shellOutputPath) {
        // 直接写入脚本
        // windows：craft.bat
        StringBuilder scriptBuilder = new StringBuilder();
        scriptBuilder.append("@echo off\n");
        scriptBuilder.append(String.format("java -jar %s %%*", jarPath)).append("\n");
        FileUtil.writeBytes(scriptBuilder.toString().getBytes(), shellOutputPath + ".bat");

        // other: craft
        scriptBuilder = new StringBuilder();
        scriptBuilder.append("#!/bin/bash\n");
        scriptBuilder.append(String.format("java -jar %s \"$@\"", jarPath)).append("\n");
        FileUtil.writeBytes(scriptBuilder.toString().getBytes(), shellOutputPath);
        try {
            // 赋予执行权限
            Set<PosixFilePermission> permissions = PosixFilePermissions.fromString("rwxrwxrwx");
            Files.setPosixFilePermissions(Paths.get(shellOutputPath), permissions);
        } catch (IOException ignored) {
        }
    }
}
