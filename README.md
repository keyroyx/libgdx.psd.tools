# gdx.keyroy.psd.tools
</br>
一个 PSD数据 转换 JSON数据 的工具 </br>
</br>
可以用来编辑简单的界面工具 </br>
为了 方便 LIBGDX 的初学者而开发的工具</br>
功能包括 , 分析PSD图层信息 ,打包图片 , 设置图层参数</br>
测试代码中 , 有 PSD 转换 libgdx 组件的 Demo (libgdx-1.6.1)</br>
</br>
使用帮助 </br>
导入PSD文件的方法 </br>
拖拽PSD文件 或者 文件夹 , 到 PSD文件列表 (左上面板)</br>
导入INI文件的方法 </br>
拖拽INI文件 或者 文件夹 , 到 INI文件列表 (左下面板)</br>
</br>
INI 的参数设定方式 有两种</br>
A : key=val (表现为 文本编辑方式 , 输入字符串)</br>
B : key=\[v1,v2,v3\] (表现为 使用选择方式 , 选择数值 , 这个数值不能自定义或者修改)</br>
</br>
INI 参数 只可以设定到 图层上 </br>
添加方式 : 在 数据图层结构 (右上面板) , 单选节点 , 右键->添加事件->选择属性->编辑属性数值</br>
删除方式 : 在 数据参数列表 (右下面板) , 单选, 或者多选 , 右键 ->删除图层参数</br>
修改方式 : 在 数据参数列表 (右下面板) , 双击 '值' 列 , 修改参数</br>
</br>

导出数据 菜单 导出 -> 打包 </br>
</br>
PSD 例子 </br>
![](https://github.com/keyroyx/resource.keyroy.screenshot/blob/master/resource.keyroy.screenshot/psd.jpg)
</br>
libgdx 测试类 PsdReflectTest 的运行截图 </br>
![](https://github.com/keyroyx/resource.keyroy.screenshot/blob/master/resource.keyroy.screenshot/gdx.jpg)

#注 : 各别新版本的PSD数据, PSD库不能解析成功 <br>测试版本为 Adobe Photoshop 版本: 14.0
