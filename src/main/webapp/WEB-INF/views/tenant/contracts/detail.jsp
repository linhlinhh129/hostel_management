<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
  <%@ taglib prefix="c" uri="jakarta.tags.core" %>
    <%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
      <c:set var="ctx" value="${pageContext.request.contextPath}" />
      <c:set var="pageTitle" value="Chi tiết Hợp đồng ${contract.code}" />
      <c:set var="pageRole" value="TENANT" />
      <c:set var="activeMenu" value="contracts" />
      <jsp:include page="/WEB-INF/views/layout/head.jsp" />

      <%-- PII Masking thực hiện thuần JSTL --%>
      <c:set var="maskedCccd" value="---" />
      <c:if test="${not empty contract && not empty contract.tenantIdentityNumber}">
        <c:set var="rawCccd" value="${contract.tenantIdentityNumber}" />
        <c:choose>
          <c:when test="${rawCccd.length() >= 7}">
            <c:set var="maskedCccd" value="${rawCccd.substring(0, 3)}*****${rawCccd.substring(rawCccd.length() - 4)}" />
          </c:when>
          <c:otherwise>
            <c:set var="maskedCccd" value="${rawCccd}" />
          </c:otherwise>
        </c:choose>
      </c:if>

      <c:set var="maskedPhone" value="---" />
      <c:if test="${not empty contract && not empty contract.tenantPhone}">
        <c:set var="rawPhone" value="${contract.tenantPhone}" />
        <c:choose>
          <c:when test="${rawPhone.length() >= 5}">
            <c:set var="maskedPhone" value="${rawPhone.substring(0, 2)}*****${rawPhone.substring(rawPhone.length() - 3)}" />
          </c:when>
          <c:otherwise>
            <c:set var="maskedPhone" value="${rawPhone}" />
          </c:otherwise>
        </c:choose>
      </c:if>



          <body>
            <div class="app-shell">
              <jsp:include page="/WEB-INF/views/layout/sidebar.jsp" />
              <div class="sidebar-overlay"></div>
              <div class="main-wrapper">
                <jsp:include page="/WEB-INF/views/layout/topbar.jsp" />
                <main class="page-content">
                  <jsp:include page="/WEB-INF/views/layout/alerts.jsp" />

                  <div
                    class="page-header hero-sky-gradient d-flex flex-wrap justify-content-between align-items-center gap-3"
                    style="border-radius:var(--hms-radius-lg);margin-bottom:1.75rem">
                    <div>
                      <c:choose>
                        <c:when test="${not empty contract}">
                          <h1>
                            Chi tiết Hợp đồng:
                            <c:out value="${contract.code}" />
                            <c:choose>
                              <c:when test="${contract.status == 'ACTIVE'}">
                                <span class="badge-hms badge-success ms-2">Đang hiệu lực</span>
                              </c:when>
                              <c:otherwise>
                                <span class="badge-hms badge-neutral ms-2">Đã kết thúc</span>
                              </c:otherwise>
                            </c:choose>
                          </h1>
                          <p>Phòng: <span class="fw-bold">
                              <c:out value="${contract.room.code}" />
                            </span></p>
                        </c:when>
                        <c:otherwise>
                          <h1>Hợp đồng của tôi</h1>
                          <p>Chưa có hợp đồng trong hệ thống</p>
                        </c:otherwise>
                      </c:choose>
                    </div>
                    <c:if test="${not empty contract}">
                      <div class="d-flex gap-2 align-items-center mt-2 mt-md-0">
                        <button onclick="window.print()" class="btn-mintlify-primary">
                          <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor"
                            stroke-width="2" style="margin-right:6px">
                            <polyline points="6 9 6 2 18 2 18 9"></polyline>
                            <path d="M6 18H4a2 2 0 0 1-2-2v-5a2 2 0 0 1 2-2h16a2 2 0 0 1 2 2v5a2 2 0 0 1-2 2h-2"></path>
                            <rect x="6" y="14" width="12" height="8"></rect>
                          </svg>
                          In Hợp Đồng / Lưu PDF
                        </button>
                      </div>
                    </c:if>
                  </div>

                  <div class="document-viewer-wrapper mt-4">
                    <!-- A4 Document embedded visually inside the dashboard -->
                    <div class="a4-container">
                      <c:choose>
                        <c:when test="${empty contract}">
                          <%-- Empty state: tenant chưa có hợp đồng --%>
                            <div style="text-align:center;padding:4rem 2rem">
                              <svg width="56" height="56" viewBox="0 0 24 24" fill="none" stroke="var(--hms-stone)"
                                stroke-width="1.5" style="margin-bottom:1rem">
                                <path d="M14 2H6a2 2 0 0 0-2 2v16a2 2 0 0 0 2 2h12a2 2 0 0 0 2-2V8z" />
                                <polyline points="14 2 14 8 20 8" />
                                <line x1="16" y1="13" x2="8" y2="13" />
                                <line x1="16" y1="17" x2="8" y2="17" />
                              </svg>
                              <h4 style="font-weight:700;margin:0 0 0.5rem">Chưa có hợp đồng</h4>
                              <p style="color:var(--hms-stone);font-size:0.9rem;margin:0">
                                Bạn chưa có hợp đồng thuê nào trong hệ thống.<br>
                                Vui lòng liên hệ Ban quản lý để biết thêm thông tin.
                              </p>
                            </div>
                        </c:when>
                        <c:otherwise>
                          <div class="text-center mb-4">
                            <h4 class="mb-1">CỘNG HÒA XÃ HỘI CHỦ NGHĨA VIỆT NAM</h4>
                            <h5 class="mb-2"><u>Độc lập - Tự do - Hạnh phúc</u></h5>
                          </div>

                          <h3 class="text-center mt-4 mb-4">HỢP ĐỒNG THUÊ PHÒNG TRỌ</h3>

                          <p>Hôm nay, ngày <strong>
                              <c:out value="${contract.signedDay}" />/
                              <c:out value="${contract.signedMonth}" />/
                              <c:out value="${contract.signedYear}" />
                            </strong>, tại địa chỉ: <strong>
                              <c:out value="${contract.facility.address}" />
                            </strong></p>
                          <p>Chúng tôi gồm:</p>

                          <div class="mt-3 mb-3">
                            <p class="text-bold">1. Đại diện bên cho thuê phòng trọ (Bên A)</p>
                            <p>Ông/Bà: <strong>
                                <c:out value="${contract.manager.fullName}" />
                              </strong></p>
                            <p>Sinh ngày: <strong>
                                <c:out value="${contract.manager.dob}" />
                              </strong></p>
                            <p>CMND/CCCD số: <strong>
                                <c:out value="${contract.manager.identityNumber}" />
                              </strong>, cấp tại: <strong>Cục Cảnh sát quản lý hành chính về trật tự xã hội</strong></p>
                            <p>Số điện thoại: <strong>
                                <c:out value="${contract.manager.phone}" />
                              </strong></p>
                          </div>

                          <div class="mb-3">
                            <p class="text-bold">2. Bên thuê phòng trọ (Bên B)</p>
                            <p>Ông/Bà: <strong>
                                <c:out value="${contract.tenantFullName}" />
                              </strong></p>
                            <p>Sinh ngày: <strong>
                                <c:out value="${contract.tenantDob}" />
                              </strong></p>
                            <p>Nơi đăng ký hộ khẩu thường trú: <strong>
                                <c:out value="${contract.tenantPermanentAddress}" />
                              </strong></p>
                            <%-- CCCD/CMND đã được mask phía server (business.md 4.2) --%>
                              <p>Số CMND/CCCD: <strong>
                                  <c:out value="${maskedCccd}" />
                                </strong>, cấp ngày <strong>
                                  <c:out value="${contract.tenantIdentityIssueDate}" />
                                </strong>, tại <strong>
                                  <c:out value="${contract.tenantIdentityIssuePlace}" />
                                </strong></p>
                              <%-- SĐT đã được mask phía server (business.md 4.2) --%>
                                <p>Số điện thoại: <strong>
                                    <c:out value="${maskedPhone}" />
                                  </strong></p>
                          </div>

                          <p>Sau khi bàn bạc trên tinh thần dân chủ, hai bên cùng có lợi, cùng thống nhất như sau:</p>

                          <p class="text-bold mt-4" style="text-decoration: underline">Điều 1: Nội dung thuê phòng</p>
                          <p>Bên A đồng ý cho bên B thuê 01 phòng ở tại địa chỉ: <strong>
                              <c:out value="${contract.facility.address}" />
                            </strong></p>
                          <p>Trong phòng gồm có:</p>
                          <ul>
                            <li>01 bình nóng lạnh</li>
                            <li>01 máy điều hòa và 01 điều khiển</li>
                            <li>01 tủ quần áo</li>
                            <li>01 tủ bếp</li>
                            <li>01 giường ngủ</li>
                            <li>01 bàn học và ghế tựa</li>
                            <li>Thiết bị vệ sinh</li>
                            <li>Đèn chiếu sáng đầy đủ</li>
                          </ul>

                          <p class="text-bold mt-4" style="text-decoration: underline">Điều 2: Giá thuê và hình thức
                            thanh toán</p>
                          <p>Giá thuê: <strong>
                              <fmt:formatNumber value="${not empty contract.room && not empty contract.room.roomFee ? contract.room.roomFee : 0}" pattern="#,##0" /> đ/tháng
                            </strong></p>
                          <p>Bằng chữ: <strong>
                              <c:out value="${contract.amountInWords}" />
                            </strong></p>
                          <p>Phòng số: <strong>
                              <c:out value="${contract.room.roomLabel}" />
                            </strong></p>
                          <p>Tầng: <strong>
                              <c:out value="${contract.room.floorLabel}" />
                            </strong></p>
                          <p>Hình thức thanh toán: Tiền mặt hoặc chuyển khoản vào đầu tháng, từ ngày 01 đến ngày 05 hàng
                            tháng.</p>
                          <p>Hợp đồng có giá trị kể từ <strong>
                              <c:out value="${contract.startDate}" />
                            </strong> đến <strong>
                              <c:out value="${contract.endDate}" />
                            </strong></p>
                          <p>Tiền điện: <strong>
                              <fmt:formatNumber value="${contract.facility != null ? contract.facility.electricityPrice : 0}" pattern="#,##0" /> đ/số
                            </strong>, tính theo chỉ số công tơ, thanh toán vào cuối các tháng.</p>
                          <p>Tiền nước: <strong>
                              <fmt:formatNumber
                                value="${contract.facility != null && contract.facility.waterPrice != null && contract.facility.waterPrice > 0 ? contract.facility.waterPrice : 25000}"
                                pattern="#,##0" /> đ/m³
                            </strong>, tính theo chỉ số đồng hồ nước, thanh toán vào cuối các tháng.</p>
                          <p>Tiền Internet: <strong>
                              <fmt:formatNumber value="${contract.facility != null ? contract.facility.internetFee : 0}" pattern="#,##0" />
                              đ/người/tháng
                            </strong></p>
                          <p>Tiền dịch vụ: <strong>
                              <fmt:formatNumber value="${contract.facility != null ? contract.facility.serviceFee : 0}" pattern="#,##0" /> đ/người/tháng
                            </strong></p>
                          <p
                            style="font-style: italic; color: #4b5563; margin-top: 4px; margin-bottom: 12px; font-size: 0.95em;">
                            <em>* <strong>Điều khoản đi kèm:</strong> Đơn giá điện và đơn giá nước nêu trên có thể được
                              điều chỉnh tăng hoặc giảm căn cứ theo quyết định thay đổi biểu giá của cơ quan Nhà nước có
                              thẩm quyền hoặc đơn vị cung cấp (EVN, Công ty cấp nước sạch) và phải thông báo trước cho
                              Bên thuê ít nhất 15 ngày.</em>
                          </p>
                          <p>Bên B đặt cọc cho bên A số tiền là: <strong>
                              <fmt:formatNumber value="${not empty contract.room && not empty contract.room.depositAmount ? contract.room.depositAmount : 0}" pattern="#,##0" /> đ
                            </strong></p>
                          <ul>
                            <li>Tiền cọc sẽ được hoàn trả đầy đủ cho bên thuê khi hợp đồng này kết thúc và bên thuê hoàn
                              trả đầy đủ chi phí thuê, bao gồm tiền phòng, điện, nước, phí dịch vụ và các chi phí khác
                              liên quan.</li>
                            <li>Trường hợp bên B hủy hợp đồng trước thời hạn, bên B sẽ không được hoàn trả số tiền đã
                              đặt cọc.</li>
                          </ul>

                          <p class="text-bold mt-4" style="text-decoration: underline">Điều 3: Trách nhiệm của các bên
                          </p>
                          <p class="text-bold">Trách nhiệm của bên A</p>
                          <ul>
                            <li>Tạo mọi điều kiện thuận lợi để bên B thực hiện theo hợp đồng.</li>
                            <li>Cung cấp nguồn điện, nước, wifi cho bên B sử dụng.</li>
                            <li>Hướng dẫn bên B chấp hành đúng các quy định của địa phương.</li>
                          </ul>
                          <p class="text-bold mt-2">Trách nhiệm của bên B</p>
                          <ul>
                            <li>Thanh toán đầy đủ các khoản tiền theo đúng thỏa thuận, đúng thời hạn từ ngày 01 đến ngày
                              05 hàng tháng. Nếu nộp muộn kể từ ngày đến hạn, mỗi ngày muộn sẽ tính bằng 1% giá trị tiền
                              phòng/tháng, mọi trường hợp khác cần sự đồng ý của bên A.</li>
                            <li>Bảo quản các trang thiết bị và cơ sở vật chất của bên A trang bị ban đầu. Nếu làm hỏng
                              phải sửa chữa, nếu mất mát phải đền bù.</li>
                            <li>Không được tự ý sửa chữa, cải tạo cơ sở vật chất, tuyệt đối không khoan đục tường khi
                              chưa được sự đồng ý của bên A. Nếu phát hiện tự ý khoan đục sẽ phạt <strong>500.000
                                đ/lần</strong>. Trường hợp tự ý khoan đục vào đường điện gây cháy nổ thì bên B phải hoàn
                              toàn chịu trách nhiệm với những thiệt hại do việc khoan đục gây ra.</li>
                            <li>Giữ gìn vệ sinh trong và ngoài khuôn viên của phòng trọ.</li>
                            <li>Tự bảo quản đồ đạc và phương tiện đi lại của mình.</li>
                            <li>Bên B phải chấp hành mọi quy định của pháp luật Nhà nước và quy định của địa phương.
                            </li>
                            <li>Nếu bên B cho khách ở qua đêm thì phải báo và được sự đồng ý của chủ nhà, đồng thời phải
                              chịu trách nhiệm về các hành vi vi phạm pháp luật của khách trong thời gian ở lại.</li>
                            <li>Bên B không được cờ bạc, buôn bán, tàng trữ ma túy, các chất cấm mà Nhà nước quy định.
                              Bên B phải tuân thủ các quy định về phòng cháy, chữa cháy, giữ gìn vệ sinh chung. Không
                              được đánh nhau, cãi nhau, chửi nhau hoặc gây mất trật tự an ninh trong khu vực cư trú.
                            </li>
                            <li>Chỉ được sử dụng bếp điện đun nấu trong khuôn viên phòng ở.</li>
                          </ul>

                          <p class="text-bold mt-4" style="text-decoration: underline">Điều 4: Trách nhiệm chung</p>
                          <ul>
                            <li>Hai bên phải tạo điều kiện cho nhau thực hiện hợp đồng.</li>
                            <li>Một trong hai bên muốn chấm dứt hợp đồng trước thời hạn thì phải báo trước cho bên kia
                              ít nhất 30 ngày và hai bên phải có sự thống nhất. Thời điểm chấm dứt hợp đồng bắt buộc
                              phải rơi vào ngày cuối cùng của tháng lịch và bàn giao lại phòng vào ngày mùng 1 đầu tháng
                              kế tiếp. Trong trường hợp Bên thuê tự ý dọn đi trước ngày cuối tháng, hợp đồng vẫn tính là
                              có hiệu lực đến hết tháng đó; Bên thuê không được hoàn lại tiền nhà cho những ngày không
                              sử dụng và phải thanh toán toàn bộ tiền điện, nước phát sinh tính đến ngày dọn đi thực tế.
                            </li>
                            <li>Trường hợp xảy ra tranh chấp hoặc một bên vi phạm hợp đồng thì hai bên cùng nhau giải
                              quyết tranh chấp. Nếu không giải quyết được thì yêu cầu cơ quan có thẩm quyền giải quyết.
                            </li>
                            <li>Hợp đồng được lập thành 02 bản có giá trị pháp lý như nhau, mỗi bên giữ 01 bản.</li>
                          </ul>

                          <div class="d-flex justify-content-between"
                            style="display: flex; justify-content: space-around; margin-top: 80px; padding-bottom: 50px;">
                            <div class="text-center">
                              <p class="text-bold">ĐẠI DIỆN BÊN B</p>
                              <p><em>(Ký, ghi rõ họ tên)</em></p>
                              <br /><br /><br /><br />
                              <p><strong>
                                  <c:out value="${contract.tenantFullName}" />
                                </strong></p>
                            </div>
                            <div class="text-center">
                              <p class="text-bold">ĐẠI DIỆN BÊN A</p>
                              <p><em>(Ký, ghi rõ họ tên)</em></p>
                              <br /><br /><br /><br />
                              <p><strong>
                                  <c:out value="${contract.manager.fullName}" />
                                </strong></p>
                            </div>
                          </div>
                    </div>
                    </c:otherwise>
                    </c:choose>
                  </div>
              </div>

              </main>
            </div>
            </div>

            <jsp:include page="/WEB-INF/views/layout/footer.jsp" />
          </body>

          </html>