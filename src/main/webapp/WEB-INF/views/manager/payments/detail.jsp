<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<c:set var="ctx" value="${pageContext.request.contextPath}" />
<c:set var="pageTitle" value="Chi tiết giao dịch - Ban Quản lý" />
<c:set var="pageRole" value="MANAGER" />
<c:set var="activeMenu" value="payments" />
<jsp:include page="/WEB-INF/views/layout/head.jsp" />

<style>
  /* Custom Page Layout & Visual Styling */
  .payment-detail-container {
    padding: 0.5rem 0 2rem 0;
  }
  .header-card {
    background: #ffffff;
    border: 1px solid var(--hms-border-soft, #eef0f2);
    border-radius: 16px;
    padding: 1.5rem;
    box-shadow: 0 4px 20px rgba(0, 0, 0, 0.02);
    margin-bottom: 2rem;
  }
  .detail-card {
    background: #ffffff;
    border: 1px solid var(--hms-border-soft, #eef0f2);
    border-radius: 16px;
    box-shadow: 0 4px 20px rgba(0, 0, 0, 0.02);
    overflow: hidden;
    height: 100%;
    display: flex;
    flex-direction: column;
  }
  .detail-card-header {
    padding: 1.25rem 1.5rem;
    background: #fdfdfd;
    border-bottom: 1px solid #f3f4f6;
    display: flex;
    align-items: center;
    justify-content: space-between;
  }
  .detail-card-header h3 {
    font-size: 1rem;
    font-weight: 700;
    color: #1f2937;
    margin: 0;
    display: flex;
    align-items: center;
    gap: 0.5rem;
  }
  .detail-card-body {
    padding: 0;
    flex-grow: 1;
  }
  .metadata-row {
    display: flex;
    justify-content: space-between;
    align-items: center;
    padding: 1.1rem 1.5rem;
    border-bottom: 1px solid #f9fafb;
    transition: background-color 0.2s ease;
  }
  .metadata-row:hover {
    background-color: #fcfdfe;
  }
  .metadata-row:last-child {
    border-bottom: none;
  }
  .meta-label {
    display: flex;
    align-items: center;
    gap: 0.75rem;
    color: #6b7280;
    font-size: 0.875rem;
    font-weight: 500;
  }
  .meta-label svg {
    color: #9ca3af;
  }
  .meta-value {
    color: #111827;
    font-size: 0.875rem;
    font-weight: 600;
    text-align: right;
  }
  .meta-value-highlight {
    font-size: 1.125rem;
    color: #10b981; /* Emerald / Brand green */
    font-weight: 700;
  }
  
  /* Status Badges */
  .badge-payment {
    padding: 0.35rem 0.75rem;
    border-radius: 9999px;
    font-size: 0.75rem;
    font-weight: 700;
    text-transform: uppercase;
    letter-spacing: 0.05em;
    display: inline-flex;
    align-items: center;
    gap: 0.25rem;
  }
  .badge-payment-pending {
    background-color: #fef3c7;
    color: #d97706;
  }
  .badge-payment-success {
    background-color: #d1fae5;
    color: #059669;
  }
  .badge-payment-rejected {
    background-color: #fee2e2;
    color: #dc2626;
  }

  /* Actions Bar */
  .action-bar {
    background: #f8fafc;
    border: 1px dashed #e2e8f0;
    border-radius: 12px;
    padding: 1.25rem;
    margin-top: 1.5rem;
    display: flex;
    justify-content: flex-end;
    align-items: center;
    gap: 0.75rem;
  }

  /* Premium Buttons */
  .btn-modern-primary {
    background: #0ea5e9;
    color: #ffffff;
    font-size: 0.875rem;
    font-weight: 600;
    padding: 0.5rem 1.25rem;
    height: 42px;
    border-radius: 10px;
    border: none;
    display: inline-flex;
    align-items: center;
    justify-content: center;
    gap: 0.5rem;
    cursor: pointer;
    box-shadow: 0 4px 12px rgba(14, 165, 233, 0.15);
    transition: all 0.2s ease;
  }
  .btn-modern-primary:hover {
    background: #0284c7;
    transform: translateY(-1px);
    box-shadow: 0 6px 16px rgba(14, 165, 233, 0.25);
  }
  
  .btn-modern-success {
    background: #10b981;
    color: #ffffff;
    font-size: 0.875rem;
    font-weight: 600;
    padding: 0.5rem 1.25rem;
    height: 42px;
    border-radius: 10px;
    border: none;
    display: inline-flex;
    align-items: center;
    justify-content: center;
    gap: 0.5rem;
    cursor: pointer;
    box-shadow: 0 4px 12px rgba(16, 185, 129, 0.15);
    transition: all 0.2s ease;
  }
  .btn-modern-success:hover {
    background: #059669;
    transform: translateY(-1px);
    box-shadow: 0 6px 16px rgba(16, 185, 129, 0.25);
  }

  .btn-modern-danger {
    background: #ef4444;
    color: #ffffff;
    font-size: 0.875rem;
    font-weight: 600;
    padding: 0.5rem 1.25rem;
    height: 42px;
    border-radius: 10px;
    border: none;
    display: inline-flex;
    align-items: center;
    justify-content: center;
    gap: 0.5rem;
    cursor: pointer;
    box-shadow: 0 4px 12px rgba(239, 68, 68, 0.15);
    transition: all 0.2s ease;
  }
  .btn-modern-danger:hover {
    background: #dc2626;
    transform: translateY(-1px);
    box-shadow: 0 6px 16px rgba(239, 68, 68, 0.25);
  }

  .btn-modern-outline {
    background: transparent;
    color: #4b5563;
    font-size: 0.875rem;
    font-weight: 600;
    padding: 0.5rem 1.25rem;
    height: 42px;
    border-radius: 10px;
    border: 1px solid #d1d5db;
    display: inline-flex;
    align-items: center;
    justify-content: center;
    gap: 0.5rem;
    cursor: pointer;
    transition: all 0.2s ease;
  }
  .btn-modern-outline:hover {
    background: #f9fafb;
    border-color: #9ca3af;
    color: #1f2937;
  }
</style>

<body>
  <div class="app-shell">
    <jsp:include page="/WEB-INF/views/layout/sidebar.jsp" />
    <div class="sidebar-overlay"></div>
    <div class="main-wrapper">
      <jsp:include page="/WEB-INF/views/layout/topbar.jsp" />
      <main class="page-content">
        <jsp:include page="/WEB-INF/views/layout/alerts.jsp" />

        <div class="payment-detail-container">
          
          <%-- Header Card --%>
          <div class="header-card d-flex flex-wrap justify-content-between align-items-center gap-3">
            <div>
              <div class="d-flex align-items-center gap-3 flex-wrap">
                <h1 style="margin: 0; font-size: 1.5rem; font-weight: 800; letter-spacing: -0.5px; color: #111827;">
                  Giao dịch <c:out value="${payment.transactionCode}" />
                </h1>
                <c:choose>
                  <c:when test="${payment.status == 'PENDING'}">
                    <span class="badge-payment badge-payment-pending">
                      <span style="display:inline-block; width:6px; height:6px; background:#d97706; border-radius:50%"></span>
                      Chờ duyệt
                    </span>
                  </c:when>
                  <c:when test="${payment.status == 'SUCCESS'}">
                    <span class="badge-payment badge-payment-success">
                      <span style="display:inline-block; width:6px; height:6px; background:#059669; border-radius:50%"></span>
                      Thành công
                    </span>
                  </c:when>
                  <c:otherwise>
                    <span class="badge-payment badge-payment-rejected">
                      <span style="display:inline-block; width:6px; height:6px; background:#dc2626; border-radius:50%"></span>
                      Từ chối
                    </span>
                  </c:otherwise>
                </c:choose>
              </div>
              <p style="margin: 6px 0 0 0; color: #6b7280; font-size: 0.9rem;">
                Người thực hiện: <strong style="color: #111827;"><c:out value="${payment.tenantName}" default="N/A" /></strong>
              </p>
            </div>
            
            <div class="d-flex align-items-center gap-2">
              <a href="${ctx}/manager/payments" class="btn-modern-outline text-decoration-none">
                <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                  <line x1="19" y1="12" x2="5" y2="12"></line>
                  <polyline points="12 19 5 12 12 5"></polyline>
                </svg>
                Quay lại
              </a>
            </div>
          </div>

          <%-- Grid Layout --%>
          <div class="row g-4">
            
            <%-- Left Column: Transaction Details --%>
            <div class="col-lg-6">
              <div class="detail-card">
                <div class="detail-card-header">
                  <h3>
                    <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5" stroke-linecap="round" stroke-linejoin="round" style="color: #0ea5e9;">
                      <rect x="2" y="5" width="20" height="14" rx="2" ry="2"></rect>
                      <line x1="2" y1="10" x2="22" y2="10"></line>
                    </svg>
                    Thông tin giao dịch
                  </h3>
                </div>
                <div class="detail-card-body">
                  
                  <div class="metadata-row">
                    <span class="meta-label">
                      <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                        <path d="M20 21v-2a4 4 0 0 0-4-4H8a4 4 0 0 0-4 4v2"></path>
                        <circle cx="12" cy="7" r="4"></circle>
                      </svg>
                      Người nộp tiền
                    </span>
                    <span class="meta-value">
                      <c:out value="${payment.tenantName}" default="N/A" />
                    </span>
                  </div>

                  <div class="metadata-row">
                    <span class="meta-label">
                      <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                        <path d="M22 16.92v3a2 2 0 0 1-2.18 2 19.79 19.79 0 0 1-8.63-3.07 19.5 19.5 0 0 1-6-6 19.79 19.79 0 0 1-3.07-8.67A2 2 0 0 1 4.11 2h3a2 2 0 0 1 2 1.72 12.84 12.84 0 0 0 .7 2.81 2 2 0 0 1-.45 2.11L8.09 9.91a16 16 0 0 0 6 6l1.27-1.27a2 2 0 0 1 2.11-.45 12.84 12.84 0 0 0 2.81.7A2 2 0 0 1 22 16.92z"></path>
                      </svg>
                      Số điện thoại
                    </span>
                    <span class="meta-value">
                      <c:out value="${payment.tenantPhone}" default="N/A" />
                    </span>
                  </div>

                  <div class="metadata-row">
                    <span class="meta-label">
                      <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                        <path d="M4 4h16c1.1 0 2 .9 2 2v12c0 1.1-.9 2-2 2H4c-1.1 0-2-.9-2-2V6c0-1.1.9-2 2-2z"></path>
                        <polyline points="22,6 12,13 2,6"></polyline>
                      </svg>
                      Email
                    </span>
                    <span class="meta-value">
                      <c:out value="${payment.tenantEmail}" default="N/A" />
                    </span>
                  </div>

                  <div class="metadata-row">
                    <span class="meta-label">
                      <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                        <path d="M3 9l9-7 9 7v11a2 2 0 0 1-2 2H5a2 2 0 0 1-2-2z"></path>
                        <polyline points="9 22 9 12 15 12 15 22"></polyline>
                      </svg>
                      Cơ sở
                    </span>
                    <span class="meta-value" style="font-weight: 500;">
                      <c:out value="${payment.facilityName}" /><br>
                      <small style="color: #6b7280; font-weight: 400;"><c:out value="${payment.facilityAddress}" /></small>
                    </span>
                  </div>

                  <div class="metadata-row">
                    <span class="meta-label">
                      <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                        <rect x="3" y="3" width="18" height="18" rx="2" ry="2"></rect>
                        <line x1="9" y1="3" x2="9" y2="21"></line>
                      </svg>
                      Mã phòng
                    </span>
                    <span class="meta-value">
                      <span class="badge-payment badge-payment-pending" style="color: #0369a1; background-color: #e0f2fe;">
                        Phòng <c:out value="${payment.roomCode}" />
                      </span>
                    </span>
                  </div>

                  <div class="metadata-row">
                    <span class="meta-label">
                      <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                        <line x1="12" y1="1" x2="12" y2="23"></line>
                        <path d="M17 5H9.5a3.5 3.5 0 0 0 0 7h5a3.5 3.5 0 0 1 0 7H6"></path>
                      </svg>
                      Số tiền đã thanh toán
                    </span>
                    <span class="meta-value-highlight">
                      <fmt:formatNumber value="${payment.amount}" pattern="#,##0" /> đ
                    </span>
                  </div>

                  <div class="metadata-row">
                    <span class="meta-label">
                      <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                        <rect x="3" y="4" width="18" height="18" rx="2" ry="2"></rect>
                        <line x1="16" y1="2" x2="16" y2="6"></line>
                        <line x1="8" y1="2" x2="8" y2="6"></line>
                        <line x1="3" y1="10" x2="21" y2="10"></line>
                      </svg>
                      Ngày nộp tiền
                    </span>
                    <span class="meta-value">
                      <c:out value="${payment.paymentDate}" />
                    </span>
                  </div>

                  <div class="metadata-row">
                    <span class="meta-label">
                      <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                        <circle cx="12" cy="12" r="10"></circle>
                        <polyline points="12 6 12 12 16 14"></polyline>
                      </svg>
                      Thời gian tạo yêu cầu
                    </span>
                    <span class="meta-value">
                      <c:out value="${payment.createdAt}" />
                    </span>
                  </div>

                  <div class="metadata-row">
                    <span class="meta-label">
                      <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                        <rect x="2" y="4" width="20" height="16" rx="2" ry="2"></rect>
                        <line x1="12" y1="4" x2="12" y2="20"></line>
                      </svg>
                      Phương thức
                    </span>
                    <span class="meta-value">
                      <c:choose>
                        <c:when test="${payment.paymentMethod == 'BANK_TRANSFER'}">Chuyển khoản ngân hàng</c:when>
                        <c:when test="${payment.paymentMethod == 'CASH'}">Tiền mặt</c:when>
                        <c:otherwise><c:out value="${payment.paymentMethod}" /></c:otherwise>
                      </c:choose>
                    </span>
                  </div>

                </div>
              </div>
            </div>

            <%-- Right Column: Associated Invoice --%>
            <div class="col-lg-6">
              <div class="detail-card d-flex flex-column justify-content-between">
                <div>
                  <div class="detail-card-header">
                    <h3>
                      <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5" stroke-linecap="round" stroke-linejoin="round" style="color: #10b981;">
                        <path d="M14 2H6a2 2 0 0 0-2 2v16a2 2 0 0 0 2 2h12a2 2 0 0 0 2-2V8z"></path>
                        <polyline points="14 2 14 8 20 8"></polyline>
                        <line x1="16" y1="13" x2="8" y2="13"></line>
                        <line x1="16" y1="17" x2="8" y2="17"></line>
                        <polyline points="10 9 9 9 8 9"></polyline>
                      </svg>
                      Hóa đơn liên quan
                    </h3>
                  </div>
                  <div class="detail-card-body">
                    <c:choose>
                      <c:when test="${not empty payment.invoiceCode}">
                        
                        <div class="metadata-row">
                          <span class="meta-label">
                            <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                              <rect x="3" y="3" width="18" height="18" rx="2" ry="2"></rect>
                              <line x1="3" y1="9" x2="21" y2="9"></line>
                              <line x1="9" y1="21" x2="9" y2="9"></line>
                            </svg>
                            Mã hóa đơn
                          </span>
                          <span class="meta-value">
                            <c:out value="${payment.invoiceCode}" />
                          </span>
                        </div>

                        <div class="metadata-row">
                          <span class="meta-label">
                            <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                              <rect x="3" y="4" width="18" height="18" rx="2" ry="2"></rect>
                              <line x1="16" y1="2" x2="16" y2="6"></line>
                              <line x1="8" y1="2" x2="8" y2="6"></line>
                              <line x1="3" y1="10" x2="21" y2="10"></line>
                            </svg>
                            Hạn thanh toán
                          </span>
                          <span class="meta-value">
                            <c:out value="${payment.dueDate}" />
                          </span>
                        </div>

                        <div class="metadata-row">
                          <span class="meta-label">
                            <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                              <line x1="12" y1="1" x2="12" y2="23"></line>
                              <path d="M17 5H9.5a3.5 3.5 0 0 0 0 7h5a3.5 3.5 0 0 1 0 7H6"></path>
                            </svg>
                            Tổng tiền hóa đơn
                          </span>
                          <span class="meta-value" style="font-weight:700;">
                            <fmt:formatNumber value="${payment.invoiceTotal}" pattern="#,##0" /> đ
                          </span>
                        </div>

                        <div class="metadata-row" style="align-items: flex-start;">
                          <span class="meta-label" style="padding-top: 2px;">
                            <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                              <path d="M21 15a2 2 0 0 1-2 2H7l-4 4V5a2 2 0 0 1 2-2h14a2 2 0 0 1 2 2z"></path>
                            </svg>
                            Ghi chú hóa đơn
                          </span>
                          <span class="meta-value" style="max-width: 60%; font-weight: 500; color: #4b5563; word-break: break-word;">
                            <c:out value="${payment.invoiceNote}" default="Không có ghi chú" />
                          </span>
                        </div>

                      </c:when>
                      <c:otherwise>
                        <div class="text-center p-5" style="margin: 2rem 1.5rem; border: 2px dashed #e2e8f0; border-radius: 12px; background: #f8fafc;">
                          <svg width="32" height="32" viewBox="0 0 24 24" fill="none" stroke="#94a3b8" stroke-width="2" stroke-linecap="round" stroke-linejoin="round" style="margin-bottom: 0.75rem;">
                            <path d="M14 2H6a2 2 0 0 0-2 2v16a2 2 0 0 0 2 2h12a2 2 0 0 0 2-2V8z"></path>
                            <polyline points="14 2 14 8 20 8"></polyline>
                            <line x1="9" y1="15" x2="15" y2="15"></line>
                          </svg>
                          <p style="color: #64748b; margin: 0; font-size: 0.875rem; font-weight: 500;">
                            Không tìm thấy hóa đơn liên quan cho giao dịch này.
                          </p>
                        </div>
                      </c:otherwise>
                    </c:choose>
                  </div>
                </div>

                <%-- Action Row (Duyệt/Từ chối) --%>
                <c:if test="${payment.status == 'PENDING' || payment.status == 'REJECTED'}">
                  <div style="padding: 1.5rem; border-top: 1px solid #f3f4f6; background-color: #fafbfc;">
                    <div style="font-size: 0.8125rem; font-weight: 600; color: #475569; margin-bottom: 0.75rem; display: flex; align-items: center; gap: 0.35rem;">
                      <svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                        <circle cx="12" cy="12" r="10"></circle>
                        <line x1="12" y1="8" x2="12" y2="12"></line>
                        <line x1="12" y1="16" x2="12.01" y2="16"></line>
                      </svg>
                      Thao tác duyệt giao dịch dành cho Quản lý
                    </div>
                    
                    <div class="d-flex gap-2 justify-content-end">
                      
                      <c:if test="${payment.status == 'PENDING'}">
                        <form action="${ctx}/manager/payments/${payment.paymentId}/reject" method="POST" class="m-0" style="flex: 1;">
                          <input type="hidden" name="csrfToken" value="${csrfToken}">
                          <button type="submit" class="btn-modern-danger w-100" onclick="return confirm('Bạn có chắc chắn muốn TỪ CHỐI giao dịch này?');">
                            <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                              <line x1="18" y1="6" x2="6" y2="18"></line>
                              <line x1="6" y1="6" x2="18" y2="18"></line>
                            </svg>
                            Từ chối
                          </button>
                        </form>
                        
                        <form action="${ctx}/manager/payments/${payment.paymentId}/approve" method="POST" class="m-0" style="flex: 1;">
                          <input type="hidden" name="csrfToken" value="${csrfToken}">
                          <button type="submit" class="btn-modern-success w-100" onclick="return confirm('Bạn có chắc chắn duyệt giao dịch này?');">
                            <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5" stroke-linecap="round" stroke-linejoin="round">
                              <polyline points="20 6 9 17 4 12"></polyline>
                            </svg>
                            Xác nhận duyệt
                          </button>
                        </form>
                      </c:if>

                      <c:if test="${payment.status == 'REJECTED'}">
                        <form action="${ctx}/manager/payments/${payment.paymentId}/approve" method="POST" class="m-0" style="width: 100%;">
                          <input type="hidden" name="csrfToken" value="${csrfToken}">
                          <button type="submit" class="btn-modern-success w-100" onclick="return confirm('Bạn có chắc chắn muốn duyệt lại giao dịch này từ BỊ TỪ CHỐI thành THÀNH CÔNG?');">
                            <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5" stroke-linecap="round" stroke-linejoin="round">
                              <polyline points="20 6 9 17 4 12"></polyline>
                            </svg>
                            Duyệt giao dịch bị từ chối
                          </button>
                        </form>
                      </c:if>

                    </div>
                  </div>
                </c:if>

              </div>
            </div>
            
          </div><%-- end row --%>
          
        </div><%-- end container --%>

      </main>
    </div>
  </div>
  <jsp:include page="/WEB-INF/views/layout/footer.jsp" />
</body>
</html>