package io.github.dingxinliang88.maker.generator;

import cn.hutool.core.io.FileUtil;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.attribute.PosixFilePermission;
import java.nio.file.attribute.PosixFilePermissions;
import java.util.Set;

/**
 * @author <a href="https://github.com/dingxinliang88">youyi</a>
 */
public class ScriptGenerator {

    public static void doGenerate(final String jarPath, String dest) {
        // 直接写入脚本
        // windows
        StringBuilder scriptBuilder = new StringBuilder();
        scriptBuilder.append("@echo off\n");
        scriptBuilder.append(String.format("java -jar %s %%*", jarPath)).append("\n");
        FileUtil.writeBytes(scriptBuilder.toString().getBytes(), dest + ".bat");

        // other
        scriptBuilder = new StringBuilder();
        scriptBuilder.append("#!/bin/bash\n");
        scriptBuilder.append(String.format("java -jar %s \"$@\"", jarPath)).append("\n");
        FileUtil.writeBytes(scriptBuilder.toString().getBytes(), dest);
        try {
            // 赋予执行权限
            Set<PosixFilePermission> permissions = PosixFilePermissions.fromString("rwxrwxrwx");
            Files.setPosixFilePermissions(Paths.get(dest), permissions);
        } catch (IOException ignored) {

        }
    }

    public static void main(String[] args) {
        String dest = System.getProperty("user.dir") + File.separator + "generator";
        doGenerate("", dest);
    }
}
