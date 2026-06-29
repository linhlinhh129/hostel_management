<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
  <%@ taglib prefix="c" uri="jakarta.tags.core" %>
    <%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
      <c:set var="ctx" value="${pageContext.request.contextPath}" />
      <c:set var="pageTitle" value="Chi tiết giao dịch - BQL" />
      <c:set var="pageRole" value="MANAGER" />
      <c:set var="activeMenu" value="payments" />
      <jsp:include page="/WEB-INF/views/layout/head.jsp" />

      <body>
        <div class="app-shell">
          <jsp:include page="/WEB-INF/views/layout/sidebar.jsp" />
          <div class="sidebar-overlay"></div>
          <div class="main-wrapper">
            <jsp:include page="/WEB-INF/views/layout/topbar.jsp" />
            <main class="page-content">
              <jsp:include page="/WEB-INF/views/layout/alerts.jsp" />

              <%-- Header --%>
                <div
                  class="page-header hero-sky-gradient d-flex flex-wrap justify-content-between align-items-start gap-3">
                  <div>
                    <div style="font-size:0.8125rem;color:var(--hms-text-muted);margin-bottom:6px">
                      <a href="${ctx}/manager/payments" style="color:var(--hms-text-muted);text-decoration:none">← Danh
                        sách giao dịch</a>
                    </div>
                    <div class="d-flex align-items-center gap-2 flex-wrap">
                      <h1 style="margin:0;font-family:monospace">
                        <c:out value="${payment.transactionCode}" />
                      </h1>
                      <c:choose>
                        <c:when test="${payment.status == 'PENDING'}">
                          <span class="badge-hms badge-warning" style="font-size:0.9rem">Chờ duyệt</span>
                        </c:when>
                        <c:when test="${payment.status == 'SUCCESS'}">
                          <span class="badge-hms badge-success" style="font-size:0.9rem">Thành công</span>
                        </c:when>
                        <c:otherwise>
                          <span class="badge-hms badge-danger" style="font-size:0.9rem">Từ chối</span>
                        </c:otherwise>
                      </c:choose>
                    </div>
                    <p style="margin-top:4px">
                      Người thuê: <strong>
                        <c:out value="${payment.tenantName}" />
                      </strong>
                    </p>
                  </div>
                  <div class="d-flex gap-2 flex-wrap">
                    <c:if test="${payment.status == 'PENDING'}">
                      <form action="${ctx}/manager/payments/${payment.paymentId}/approve" method="POST" class="m-0">
                        <input type="hidden" name="csrfToken" value="${csrfToken}">
                        <button type="submit" class="quick-action-btn primary"
                          onclick="return confirm('Bạn có chắc chắn duyệt giao dịch này?');">
                          <svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor"
                            stroke-width="2" style="margin-right:5px">
                            <polyline points="20 6 9 17 4 12"></polyline>
                          </svg>
                          Xác nhận
                        </button>
                      </form>
                      <form action="${ctx}/manager/payments/${payment.paymentId}/reject" method="POST" class="m-0">
                        <input type="hidden" name="csrfToken" value="${csrfToken}">
                        <!-- Use a custom danger button style or reuse default -->
                        <button type="submit" class="quick-action-btn"
                          style="color:var(--hms-accent-danger);border-color:var(--hms-accent-danger);background:transparent"
                          onmouseover="this.style.background='var(--hms-accent-danger)';this.style.color='#fff';"
                          onmouseout="this.style.background='transparent';this.style.color='var(--hms-accent-danger)';"
                          onclick="return confirm('Xác nhận TỪ CHỐI giao dịch này?');">
                          <svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor"
                            stroke-width="2" style="margin-right:5px">
                            <line x1="18" y1="6" x2="6" y2="18"></line>
                            <line x1="6" y1="6" x2="18" y2="18"></line>
                          </svg>
                          Từ chối
                        </button>
                      </form>
                    </c:if>
                    <c:if test="${payment.status == 'REJECTED'}">
                      <form action="${ctx}/manager/payments/${payment.paymentId}/approve" method="POST" class="m-0">
                        <input type="hidden" name="csrfToken" value="${csrfToken}">
                        <button type="submit" class="quick-action-btn primary"
                          onclick="return confirm('Bạn có chắc chắn muốn duyệt lại giao dịch này từ BỊ TỪ CHỐI thành THÀNH CÔNG?');">
                          <svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor"
                            stroke-width="2" style="margin-right:5px">
                            <polyline points="20 6 9 17 4 12"></polyline>
                          </svg>
                          Duyệt giao dịch
                        </button>
                      </form>
                    </c:if>
                  </div>
                </div>

                <div class="row g-3">
                  <%-- Cột trái: Thông tin giao dịch --%>
                    <div class="col-lg-6">
                      <div class="widget-surface h-100">
                        <div class="widget-surface-header">
                          <h3>Thông tin giao dịch</h3>
                        </div>
                        <div class="widget-surface-body p-0">
                          <table style="width:100%;font-size:0.875rem;border-collapse:collapse">
                            <tr style="border-bottom:1px solid var(--hms-border)">
                              <td style="padding:10px 16px;color:var(--hms-text-muted)">Người thuê</td>
                              <td style="padding:10px 16px;text-align:right;font-weight:500">
                                <c:out value="${payment.tenantName}" />
                              </td>
                            </tr>
                            <tr style="border-bottom:1px solid var(--hms-border)">
                              <td style="padding:10px 16px;color:var(--hms-text-muted)">Số điện thoại</td>
                              <td style="padding:10px 16px;text-align:right">
                                <c:out value="${payment.tenantPhone}" default="N/A" />
                              </td>
                            </tr>
                            <tr style="border-bottom:1px solid var(--hms-border)">
                              <td style="padding:10px 16px;color:var(--hms-text-muted)">Email</td>
                              <td style="padding:10px 16px;text-align:right">
                                <c:out value="${payment.tenantEmail}" default="N/A" />
                              </td>
                            </tr>
                            <tr style="border-bottom:1px solid var(--hms-border)">
                              <td style="padding:10px 16px;color:var(--hms-text-muted)">Cơ sở</td>
                              <td style="padding:10px 16px;text-align:right">
                                <strong>
                                  <c:out value="${payment.facilityName}" />
                                </strong><br>
                                <small style="color:var(--hms-text-muted)">
                                  <c:out value="${payment.facilityAddress}" />
                                </small>
                              </td>
                            </tr>
                            <tr style="border-bottom:1px solid var(--hms-border)">
                              <td style="padding:10px 16px;color:var(--hms-text-muted)">Phòng</td>
                              <td style="padding:10px 16px;text-align:right">
                                <span class="badge-hms badge-neutral">
                                  <c:out value="${payment.roomCode}" />
                                </span>
                              </td>
                            </tr>
                            <tr style="border-bottom:1px solid var(--hms-border)">
                              <td style="padding:10px 16px;color:var(--hms-text-muted)">Số tiền thanh toán</td>
                              <td
                                style="padding:10px 16px;text-align:right;font-weight:600;color:var(--hms-accent-deep)">
                                <fmt:formatNumber value="${payment.amount}" pattern="#,##0" /> đ
                              </td>
                            </tr>
                            <tr style="border-bottom:1px solid var(--hms-border)">
                              <td style="padding:10px 16px;color:var(--hms-text-muted)">Ngày giao dịch</td>
                              <td style="padding:10px 16px;text-align:right">
                                <c:out value="${payment.paymentDate}" />
                              </td>
                            </tr>
                            <tr style="border-bottom:1px solid var(--hms-border)">
                              <td style="padding:10px 16px;color:var(--hms-text-muted)">Thời gian tạo</td>
                              <td style="padding:10px 16px;text-align:right">
                                <c:out value="${payment.createdAt}" />
                              </td>
                            </tr>
                            <tr>
                              <td style="padding:10px 16px;color:var(--hms-text-muted)">Phương thức</td>
                              <td style="padding:10px 16px;text-align:right">
                                <c:out value="${payment.paymentMethod}" />
                              </td>
                            </tr>
                          </table>
                        </div>
                      </div>
                    </div>

                    <%-- Cột phải: Thông tin hóa đơn liên quan --%>
                      <div class="col-lg-6">
                        <div class="widget-surface h-100">
                          <div class="widget-surface-header">
                            <h3>Thông tin hóa đơn liên quan</h3>
                          </div>
                          <div class="widget-surface-body p-0">
                            <c:choose>
                              <c:when test="${not empty payment.invoiceCode}">
                                <table style="width:100%;font-size:0.875rem;border-collapse:collapse">
                                  <tr style="border-bottom:1px solid var(--hms-border)">
                                    <td style="padding:10px 16px;color:var(--hms-text-muted)">Mã hóa đơn</td>
                                    <td
                                      style="padding:10px 16px;text-align:right;font-weight:500;font-family:monospace;">
                                      <c:out value="${payment.invoiceCode}" />
                                    </td>
                                  </tr>
                                  <tr style="border-bottom:1px solid var(--hms-border)">
                                    <td style="padding:10px 16px;color:var(--hms-text-muted)">Ngày hạn thanh toán</td>
                                    <td style="padding:10px 16px;text-align:right">
                                      <c:out value="${payment.dueDate}" />
                                    </td>
                                  </tr>
                                  <tr style="border-bottom:1px solid var(--hms-border)">
                                    <td style="padding:10px 16px;color:var(--hms-text-muted)">Tổng tiền hóa đơn</td>
                                    <td style="padding:10px 16px;text-align:right;font-weight:600">
                                      <fmt:formatNumber value="${payment.invoiceTotal}" pattern="#,##0" /> đ
                                    </td>
                                  </tr>
                                  <tr>
                                    <td style="padding:10px 16px;color:var(--hms-text-muted)">Ghi chú hóa đơn</td>
                                    <td style="padding:10px 16px;text-align:right">
                                      <c:out value="${payment.invoiceNote}" default="Không có ghi chú" />
                                    </td>
                                  </tr>
                                </table>
                              </c:when>
                              <c:otherwise>
                                <div class="empty-state p-5 text-center"
                                  style="border: 1px dashed var(--hms-border); border-radius:8px; margin: 16px;">
                                  <p style="color:var(--hms-text-muted);margin:0">Không có thông tin hóa đơn liên quan
                                  </p>
                                </div>
                              </c:otherwise>
                            </c:choose>
                          </div>
                        </div>
                      </div>
                </div>
            </main>
          </div>
        </div>
        <jsp:include page="/WEB-INF/views/layout/footer.jsp" />
      </body>

      </html>