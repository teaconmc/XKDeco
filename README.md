# XeKr's Decoration

## Generating stuff from the Excel file

Run this command:

```bash
py .\script\main.py
```

The generated YAML files will be generated in the `kiwi_generated` folder. The configuration files are located in the `script` folder, you
can add your downloads path to the `move_files.yaml` to make the process easier.

## Why can't I import this project?

It's probably because you should delete the proxy settings and Java path settings in the `gradle.properties` file.

## Exporting VoxelShapes of a block

1. Install Blockbench
2. File >> Plugins... >> Install the `Mod Utils` plugin
3. It's recommended to add the `Export VoxelShape (1.14+ Modded Minecraft)` button to a toolbar
4. Follow the Mod Utils' instructions to export VoxelShape (If any dialog appears, just click Confirm)
5. Paste the result to [this website](https://regex101.com/r/2hBem9/1) to beautify it
6. Create a YAML file in the `assets/xkdeco/kiwi/shape` folder and paste the result, the mod will do the basic direction-based
   transformation for you

If you need more complex shapes, please refer to the existing YAML files like the roof blocks.

---

## 从 Excel 文件生成内容

运行此命令

```bash
py .\script\main.py
```

生成的 YAML 文件将放在 `kiwi_generated` 文件夹中。配置文件位于 `script` 文件夹中。
你可以在 `move_files.yaml` 中添加你的下载路径，使过程更简单。

## 为什么我无法导入这个项目？

可能是因为你应该删除`gradle.properties`文件中的代理设置和 Java 路径设置。

## 导出块的 VoxelShape

1. 安装 Blockbench
2. 文件 >> 插件... >> 安装 `Mod Utils` 插件
3. 建议在工具栏中添加 `Export VoxelShape (1.14+ Modded Minecraft)` 按钮
4. 按照说明导出 VoxelShape（如果出现任何对话框，只需单击 "确认"）
5. 将结果粘贴到[这个网站](https://regex101.com/r/2hBem9/1)来美化它
6. 在 `assets/xkdeco/kiwi/shape` 文件夹下创建一个 YAML 文件并粘贴结果，模组会为你完成基于方向的基本转换。

如果您需要更复杂的形状，请参考现有的 YAML 文件，如屋顶块。