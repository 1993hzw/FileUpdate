java小程序，记录文件更新信息，检测上次运行后到现在的文件更新信息。（当初做这个小程序的目的是为了检测局域网里面最近是否更新了新电影，因为不想自己一个个找……）。

采用sqlite3存取数据，会生成files.db。检测结果会保存到一个文件内。