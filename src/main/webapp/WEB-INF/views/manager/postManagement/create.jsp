<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
    <%@ taglib prefix="c" uri="jakarta.tags.core" %>
        <c:set var="ctx" value="${pageContext.request.contextPath}" />
        <c:set var="pageTitle" value="Tạo bài viết - Ban Quản lý" />
        <c:set var="pageRole" value="MANAGER" />
        <c:set var="activeMenu" value="articles" />
        <jsp:include page="/WEB-INF/views/layout/head.jsp" />
        <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.7.2/css/all.min.css">

        <body>
            <div class="app-shell">
                <jsp:include page="/WEB-INF/views/layout/sidebar.jsp" />
                <div class="sidebar-overlay"></div>
                <div class="main-wrapper">
                    <jsp:include page="/WEB-INF/views/layout/topbar.jsp" />
                    <main class="page-content">
                        <jsp:include page="/WEB-INF/views/layout/alerts.jsp" />
                        <div class="page-header hero-sky-gradient d-flex flex-wrap justify-content-between align-items-start gap-3"
                            style="border-radius:var(--hms-radius-lg);margin-bottom:1.75rem">
                            <div style="position:relative;z-index:1">
                                <h1>Tạo bài viết mới</h1>
                                <p>Thêm thông báo hoặc tin tức mới cho cộng đồng cư dân</p>
                            </div>
                            <div style="display:flex;gap:8px;flex-wrap:wrap;position:relative;z-index:1">
                                <a href="${ctx}/manager/articles" class="btn-accent">Trở về danh sách</a>
                            </div>
                        </div>

                        <c:if test="${not empty error}">
                            <div class="alert alert-danger">${error}</div>
                        </c:if>

                        <div class="widget-surface">
                            <div class="widget-surface-body">
                                <form action="${ctx}/manager/articles/create?csrfToken=${csrfToken}" method="post"
                                    enctype="multipart/form-data">
                                    <input type="hidden" name="csrfToken" value="${csrfToken}" />
                                    <div class="mb-3">
                                        <label class="form-label" for="title">Tiêu đề <span
                                                class="text-danger">*</span></label>
                                        <input type="text" class="form-control" id="title" name="title" required
                                            maxlength="250">
                                    </div>
                                    <div class="mb-3">
                                        <label class="form-label" for="content">Nội dung <span
                                                class="text-danger">*</span></label>
                                        <textarea class="form-control" id="content" name="content" rows="6"
                                            required></textarea>
                                    </div>
                                    <div class="mb-3">
                                        <label class="form-label" for="image">Ảnh đính kèm (Tối đa 5MB)</label>
                                        <input type="file" class="form-control" id="image" name="image"
                                            accept="image/jpeg,image/png,image/jpg" capture="environment">
                                    </div>
                                    <div class="d-flex gap-2 mt-4">
                                        <button type="submit"
                                            class="btn-mintlify-primary px-4 py-2 d-inline-flex align-items-center gap-2">
                                            <i class="fa-solid fa-paper-plane"></i>Lưu bản tin
                                        </button>
                                        <a href="${ctx}/manager/articles/pending"
                                            class="btn-mintlify-secondary px-4 py-2 text-decoration-none d-inline-flex align-items-center gap-2">
                                            <i class="fa-solid fa-xmark"></i>Hủy
                                        </a>
                                    </div>
                                </form>
                            </div>
                        </div>
                    </main>
                </div>
            </div>
            <jsp:include page="/WEB-INF/views/layout/footer.jsp" />
        </body>

        </html>