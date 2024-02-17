# maker 制作器的使用

## 制作模板

### 示例
- `io.github.dingxinliang88.demo.example.TemplateMakerExample`
- `src/main/resources/example/springboot-init-plus`

### 使用

步骤：
1. 准备需要生成模板的源文件
2. 根据需求准备好一份配置文件（配置文件格式参考 [template-maker.json](doc/template-maker.json))
3. 执行下述代码
```java
// 指定模板文件Json配置的根目录
String rootPath = "example/springboot-init-plus/";
String configJsonStr = ResourceUtil.readUtf8Str(rootPath + "template-maker.json");
TemplateMakerConfig templateMakerConfig = JSONUtil.toBean(configJsonStr,
        TemplateMakerConfig.class);
Long id = TemplateMaker.makeTemplate(templateMakerConfig);
```
随后在目录 `.tmp/` 下会生成相应的模板文件和`meta.json`文件。

> 📢注意：maker只是工具，只是辅助生成相应的模板文件，具体的内容可能还需要根据实际的需求做调整
> 
> 详细配置还需要参见 `src/main/java/io/github/dingxinliang88/maker/template/model/TemplateMakerConfig.java`

## 制作生成器

1. 将上述步骤中生成好的 `meta.json` 文件复制到 `src/main/resources` 目录下
2. 执行 `io.github.dingxinliang88.maker.Main.main` 即可在`generated`目录下生成相应的代码生成器
