Elasticsearch 原生支持高亮：
    在查询请求中指定 highlighter 配置，ES 会在返回结果中自动将匹配的关键词用指定标签（如 <em>）包裹，并在响应的 highlight 字段中返回高亮片段。
    后端处理流程：
    构造带高亮参数的 ES 查询请求（如 highlight 字段，指定哪些字段需要高亮、前后缀标签等）。
    解析 ES 返回的结果，将高亮内容（通常在 highlight 字段）提取出来，封装到响应对象中。
    返回给前端，前端直接渲染高亮内容即可。

搜索自动补全
   ES Suggest API：
   ES 提供 suggest（补全）功能，如 completion suggester、term suggester、phrase suggester 等，支持根据用户输入实时返回补全建议。
   后端处理流程：
   前端输入时实时请求后端接口，传递当前输入内容。
   后端调用 ES 的 suggest API，获取补全建议。
   将建议列表返回给前端，前端展示下拉补全。