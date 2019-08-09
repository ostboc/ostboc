<%@ taglib prefix="shiro" uri="http://shiro.apache.org/tags" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>权限校验标签</title>
</head>
<body>
    <shiro:hasPermission name="用户管理">
        <a>用户管理</a>
    </shiro:hasPermission>

    <shiro:hasPermission name="日志管理">
        <a>日志管理</a>
    </shiro:hasPermission>
</body>
</html>
