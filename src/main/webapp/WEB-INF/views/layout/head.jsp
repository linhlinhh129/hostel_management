<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
    <%@ taglib prefix="c" uri="jakarta.tags.core" %>
        <c:set var="ctx" value="${pageContext.request.contextPath}" />
        <c:if test="${empty pageTitle}">
            <c:set var="pageTitle" value="Quản lý Nhà trọ" />
        </c:if>
        <!DOCTYPE html>
        <html lang="vi">

        <head>
            <meta charset="UTF-8">
            <meta name="viewport" content="width=device-width, initial-scale=1.0">
            <title>
                <c:out value="${pageTitle}" />
            </title>
            <%-- PWA: Web App Manifest --%>
                <link rel="manifest" href="${ctx}/manifest.json">
                <meta name="theme-color" content="#6366f1">
                <%-- PWA: iOS / Safari support --%>
                    <meta name="apple-mobile-web-app-capable" content="yes">
                    <meta name="apple-mobile-web-app-status-bar-style" content="black-translucent">
                    <meta name="apple-mobile-web-app-title" content="Innolvia Home">
                    <link rel="apple-touch-icon" href="${ctx}/assets/img/icons/icon-192x192.png">
                    <link rel="icon" type="image/png" sizes="96x96" href="${ctx}/assets/img/icons/icon-96x96.png">
                    <link rel="preconnect" href="https://fonts.googleapis.com">
                    <link rel="preconnect" href="https://fonts.gstatic.com" crossorigin>
                    <%-- Inter: toàn bộ hệ thống --%>
                        <link
                            href="https://fonts.googleapis.com/css2?family=Inter:wght@400;500;600;700;800&display=swap"
                            rel="stylesheet">
                        <link href="https://fonts.googleapis.com/css2?family=Geist+Mono:wght@400;500&display=swap"
                            rel="stylesheet">
                        <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css"
                            rel="stylesheet">
                        <link href="${ctx}/assets/css/hostel-design.css?v=42" rel="stylesheet">
                        <link href="${ctx}/assets/css/mintlify.css?v=8" rel="stylesheet">
        </head>