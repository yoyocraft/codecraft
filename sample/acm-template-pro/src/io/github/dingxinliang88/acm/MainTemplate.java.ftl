package com.juzi.acm;

import java.util.*;

/**
 * ACM 输入模板（多数之和）
 *
 * @author ${author}
 */
public class MainTemplate {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
    <#if loop>
        while (sc.hasNext()) {
    </#if>
            // 读取输入元素个数
            int n = sc.nextInt();

            // 读取数组
            int[] arr = new int[n];
            for (int i = 0; i < n; i++) {
                arr[i] = sc.nextInt();
            }

            // 处理问题逻辑，根据需要进行输出
            // 示例：计算数组元素的和
            int sum = 0;
            for (int num : arr) {
                sum += num;
            }

            System.out.println("${outputText}" + sum);
    <#if loop>
        }
    </#if>

        sc.close();
    }
}
