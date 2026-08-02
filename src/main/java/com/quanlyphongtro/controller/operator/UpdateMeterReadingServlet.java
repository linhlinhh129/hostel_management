package com.quanlyphongtro.controller.operator;
import java.time.LocalDate;

import com.quanlyphongtro.dao.AuditLogDAO;
import com.quanlyphongtro.dto.MeterStatusDTO;
import com.quanlyphongtro.dto.UserSessionDTO;
import com.quanlyphongtro.service.MeterReadingService;
import com.quanlyphongtro.util.AuditLogHelper;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import jakarta.servlet.http.Part;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

@WebServlet("/operator/meter-readings/update")
@MultipartConfig(maxFileSize = 1024 * 1024 * 5, maxRequestSize = 1024 * 1024 * 25)
public class UpdateMeterReadingServlet extends HttpServlet {
    private MeterReadingService meterReadingService;
    private final AuditLogDAO auditLogDAO = new AuditLogDAO();

    @Override
    public void init() throws ServletException {
        this.meterReadingService = new MeterReadingService();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String meterIdStr = request.getParameter("meterId");
        String roomCode = request.getParameter("roomCode");

        if (meterIdStr != null && !meterIdStr.trim().isEmpty()) {
            try {
                int meterId = Integer.parseInt(meterIdStr);
                MeterStatusDTO reading = meterReadingService.getReadingForEdit(meterId);
                if (reading != null) {
                    if (reading.isInvoicePaid()) {
                        HttpSession session = request.getSession();
                        session.setAttribute("flashMessage", "Không thể sửa vì hóa đơn kỳ này đã thanh toán.");
                        session.setAttribute("flashType", "error");
                        response.sendRedirect(request.getContextPath() + "/operator/meter-readings");
                        return;
                    }
                    request.setAttribute("meterId", reading.getMeterId());
                    request.setAttribute("roomCode", reading.getRoomCode());
                    request.setAttribute("currentElectricReading", reading.getCurrentElectricReading());
                    request.setAttribute("currentWaterReading", reading.getCurrentWaterReading());
                    request.setAttribute("previousElectricReading", reading.getPreviousElectricReading());
                    request.setAttribute("previousWaterReading", reading.getPreviousWaterReading());
                    request.setAttribute("currentElectricImg", reading.getElectricImg());
                    request.setAttribute("currentWaterImg", reading.getWaterImg());
                    
                    request.setAttribute("electricStatus", reading.getElectricStatus());
                    request.setAttribute("electricOldFinal", reading.getElectricOldFinal());
                    request.setAttribute("electricNewStart", reading.getElectricNewStart());
                    request.setAttribute("electricMaxLimit", reading.getElectricMaxLimit());
                    
                    request.setAttribute("waterStatus", reading.getWaterStatus());
                    request.setAttribute("waterOldFinal", reading.getWaterOldFinal());
                    request.setAttribute("waterNewStart", reading.getWaterNewStart());
                    request.setAttribute("waterMaxLimit", reading.getWaterMaxLimit());
                }
            } catch (NumberFormatException e) {
                // Ignore
            }
        } else if (roomCode != null && !roomCode.trim().isEmpty()) {
            request.setAttribute("roomCode", roomCode);
            LocalDate now = LocalDate.now();
            int currentMonth = now.getMonthValue();
            int currentYear = now.getYear();
            
            MeterStatusDTO previousReading = meterReadingService.getReadingBeforeCurrentMonth(roomCode, currentMonth, currentYear);
            if (previousReading == null) {
                previousReading = meterReadingService.getPreviousReadingByRoomCode(roomCode);
            }
            if (previousReading != null) {
                request.setAttribute("previousElectricReading", previousReading.getPreviousElectricReading());
                request.setAttribute("previousWaterReading", previousReading.getPreviousWaterReading());
                request.setAttribute("previousElectricMeterImageURL", previousReading.getPreviousElectricImg());
                request.setAttribute("previousWaterMeterImageURL", previousReading.getPreviousWaterImg());
            }
        }
        request.setAttribute("activeMenu", "meter-readings-update");
        request.getRequestDispatcher("/WEB-INF/views/operator/meter_readings/update.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession();
        int operatorId = 1; 
        if (session.getAttribute("currentUser") != null) {
            UserSessionDTO currentUser = (UserSessionDTO) session.getAttribute("currentUser");
            operatorId = currentUser.getId();
        }

        try {
            String meterIdStr = request.getParameter("meterId");
            String roomCode = request.getParameter("roomCode");

            if ((roomCode == null || roomCode.trim().isEmpty()) && (meterIdStr == null || meterIdStr.trim().isEmpty())) {
                session.setAttribute("flashMessage", "Dữ liệu đầu vào không hợp lệ.");
                session.setAttribute("flashType", "error");
                response.sendRedirect(request.getContextPath() + "/operator/meter-readings");
                return;
            }

            LocalDate now = LocalDate.now();
            int currentMonth = now.getMonthValue();
            int currentYear = now.getYear();

            int roomId = 0;
            int prevElectric = 0;
            int prevWater = 0;
            Integer existingMeterId = null;
            String oldElectricImgUrl = null;
            String oldWaterImgUrl = null;

            if (meterIdStr != null && !meterIdStr.trim().isEmpty()) {
                // EDIT EXISTING METER READING
                int meterId = Integer.parseInt(meterIdStr);
                MeterStatusDTO oldReading = meterReadingService.getReadingForEdit(meterId);
                if (oldReading == null) {
                    session.setAttribute("flashMessage", "Bản ghi không tồn tại.");
                    session.setAttribute("flashType", "error");
                    response.sendRedirect(request.getContextPath() + "/operator/meter-readings");
                    return;
                }
                if (oldReading.isInvoicePaid()) {
                    session.setAttribute("flashMessage", "Hóa đơn đã được thanh toán, không thể chỉnh sửa.");
                    session.setAttribute("flashType", "error");
                    response.sendRedirect(request.getContextPath() + "/operator/meter-readings");
                    return;
                }
                roomId = oldReading.getRoomId();
                roomCode = oldReading.getRoomCode();
                existingMeterId = meterId;
                prevElectric = oldReading.getPreviousElectricReading() != null ? oldReading.getPreviousElectricReading() : 0;
                prevWater = oldReading.getPreviousWaterReading() != null ? oldReading.getPreviousWaterReading() : 0;
                oldElectricImgUrl = oldReading.getElectricImg();
                oldWaterImgUrl = oldReading.getWaterImg();
            } else {
                // CREATE NEW METER READING
                MeterStatusDTO previousReading = meterReadingService.getReadingBeforeCurrentMonth(roomCode, currentMonth, currentYear);
                if (previousReading == null) {
                    previousReading = meterReadingService.getPreviousReadingByRoomCode(roomCode);
                    if (previousReading == null) {
                        session.setAttribute("flashMessage", "Mã phòng không tồn tại.");
                        session.setAttribute("flashType", "error");
                        response.sendRedirect(request.getContextPath() + "/operator/meter-readings");
                        return;
                    } else {
                        previousReading.setPreviousElectricReading(0);
                        previousReading.setPreviousWaterReading(0);
                    }
                }
                roomId = previousReading.getRoomId();
                prevElectric = previousReading.getPreviousElectricReading() != null ? previousReading.getPreviousElectricReading() : 0;
                prevWater = previousReading.getPreviousWaterReading() != null ? previousReading.getPreviousWaterReading() : 0;

                if (meterReadingService.isInvoicePaidForMonth(roomId, currentMonth, currentYear)) {
                    session.setAttribute("flashMessage", "Hóa đơn tháng này đã được thanh toán. Vui lòng chờ sang tháng sau.");
                    session.setAttribute("flashType", "error");
                    response.sendRedirect(request.getContextPath() + "/operator/meter-readings");
                    return;
                }
                
                existingMeterId = meterReadingService.checkCurrentMonthReadingExists(roomId, now.getMonthValue(), now.getYear());
            }

            // Get new readings
            int newElectric = Integer.parseInt(request.getParameter("newElectric"));
            int newWater = Integer.parseInt(request.getParameter("newWater"));

            // Get metadata
            String electricStatus = request.getParameter("electricStatus") != null && !request.getParameter("electricStatus").isEmpty() ? request.getParameter("electricStatus") : "NORMAL";
            Integer electricOldFinal = request.getParameter("electricOldFinal") != null && !request.getParameter("electricOldFinal").isEmpty() ? Integer.parseInt(request.getParameter("electricOldFinal")) : null;
            Integer electricNewStart = request.getParameter("electricNewStart") != null && !request.getParameter("electricNewStart").isEmpty() ? Integer.parseInt(request.getParameter("electricNewStart")) : null;
            Integer electricMaxLimit = request.getParameter("electricMaxLimit") != null && !request.getParameter("electricMaxLimit").isEmpty() ? Integer.parseInt(request.getParameter("electricMaxLimit")) : null;

            String waterStatus = request.getParameter("waterStatus") != null && !request.getParameter("waterStatus").isEmpty() ? request.getParameter("waterStatus") : "NORMAL";
            Integer waterOldFinal = request.getParameter("waterOldFinal") != null && !request.getParameter("waterOldFinal").isEmpty() ? Integer.parseInt(request.getParameter("waterOldFinal")) : null;
            Integer waterNewStart = request.getParameter("waterNewStart") != null && !request.getParameter("waterNewStart").isEmpty() ? Integer.parseInt(request.getParameter("waterNewStart")) : null;
            Integer waterMaxLimit = request.getParameter("waterMaxLimit") != null && !request.getParameter("waterMaxLimit").isEmpty() ? Integer.parseInt(request.getParameter("waterMaxLimit")) : null;


            // Validation AC02, AC03
            if ("NORMAL".equals(electricStatus) && newElectric < prevElectric) {
                session.setAttribute("flashMessage", "Chỉ số điện không hợp lệ. Số mới (" + newElectric + ") không được nhỏ hơn số cũ (" + prevElectric + ").");
                session.setAttribute("flashType", "error");
                response.sendRedirect(request.getContextPath() + "/operator/meter-readings");
                return;
            }
            if ("NORMAL".equals(waterStatus) && newWater < prevWater) {
                session.setAttribute("flashMessage", "Chỉ số nước không hợp lệ. Số mới (" + newWater + ") không được nhỏ hơn số cũ (" + prevWater + ").");
                session.setAttribute("flashType", "error");
                response.sendRedirect(request.getContextPath() + "/operator/meter-readings");
                return;
            }

            // Handle file uploads (AC04, AC05)
            Part electricPart = request.getPart("electricMeterImage");
            Part waterPart = request.getPart("waterMeterImage");

            if (existingMeterId == null) {
                if (electricPart == null || electricPart.getSize() == 0) {
                    session.setAttribute("flashMessage", "Vui lòng tải lên ảnh minh chứng công tơ điện.");
                    session.setAttribute("flashType", "error");
                    response.sendRedirect(request.getContextPath() + "/operator/meter-readings");
                    return;
                }
                if (waterPart == null || waterPart.getSize() == 0) {
                    session.setAttribute("flashMessage", "Vui lòng tải lên ảnh minh chứng công tơ nước.");
                    session.setAttribute("flashType", "error");
                    response.sendRedirect(request.getContextPath() + "/operator/meter-readings");
                    return;
                }
            }

            String uploadPath = getServletContext().getRealPath("") + File.separator + "uploads" + File.separator + "meter_readings";
            File uploadDir = new File(uploadPath);
            if (!uploadDir.exists()) uploadDir.mkdirs();

            String electricImgUrl = oldElectricImgUrl;
            if (electricPart != null && electricPart.getSize() > 0) {
                String electricFileName = UUID.randomUUID().toString() + "_" + getFileName(electricPart);
                electricPart.write(uploadPath + File.separator + electricFileName);
                electricImgUrl = "/uploads/meter_readings/" + electricFileName;
            }

            String waterImgUrl = oldWaterImgUrl;
            if (waterPart != null && waterPart.getSize() > 0) {
                String waterFileName = UUID.randomUUID().toString() + "_" + getFileName(waterPart);
                waterPart.write(uploadPath + File.separator + waterFileName);
                waterImgUrl = "/uploads/meter_readings/" + waterFileName;
            }

            MeterStatusDTO dtoToSave = new MeterStatusDTO();
            dtoToSave.setMeterId(existingMeterId);
            dtoToSave.setRoomId(roomId);
            dtoToSave.setCurrentElectricReading(newElectric);
            dtoToSave.setCurrentWaterReading(newWater);
            dtoToSave.setElectricImg(electricImgUrl);
            dtoToSave.setWaterImg(waterImgUrl);
            dtoToSave.setElectricStatus(electricStatus);
            dtoToSave.setElectricOldFinal(electricOldFinal);
            dtoToSave.setElectricNewStart(electricNewStart);
            dtoToSave.setElectricMaxLimit(electricMaxLimit);
            dtoToSave.setWaterStatus(waterStatus);
            dtoToSave.setWaterOldFinal(waterOldFinal);
            dtoToSave.setWaterNewStart(waterNewStart);
            dtoToSave.setWaterMaxLimit(waterMaxLimit);
            dtoToSave.setPreviousElectricReading(prevElectric);
            dtoToSave.setPreviousWaterReading(prevWater);

            boolean success;
            if (existingMeterId != null) {
                success = meterReadingService.updateMeterReading(dtoToSave);
            } else {
                success = meterReadingService.insertMeterReading(dtoToSave, operatorId);
            }

            if (success) {
                try {
                    AuditLogHelper.log(auditLogDAO, request, "rooms", roomId,
                        existingMeterId != null ? "UPDATE" : "INSERT", "Điện:" + prevElectric + " Nước:" + prevWater,
                        "Điện:" + newElectric + " Nước:" + newWater, operatorId);
                } catch (Exception ex) { /* ignore audit failure */ }
                session.setAttribute("flashMessage", "Cập nhật chỉ số điện nước cho phòng " + roomCode + " thành công.");
                session.setAttribute("flashType", "success");
                response.sendRedirect(request.getContextPath() + "/operator/meter-readings");
            } else {
                session.setAttribute("flashMessage", "Đã xảy ra lỗi khi lưu dữ liệu. Vui lòng thử lại.");
                session.setAttribute("flashType", "error");
                response.sendRedirect(request.getContextPath() + "/operator/meter-readings");
            }

        } catch (NumberFormatException e) {
            session.setAttribute("flashMessage", "Dữ liệu nhập vào không hợp lệ. Vui lòng kiểm tra lại.");
            session.setAttribute("flashType", "error");
            response.sendRedirect(request.getContextPath() + "/operator/meter-readings");
        } catch (Exception e) {
            e.printStackTrace();
            session.setAttribute("flashMessage", "Đã xảy ra lỗi hệ thống: " + e.getMessage());
            session.setAttribute("flashType", "error");
            response.sendRedirect(request.getContextPath() + "/operator/meter-readings");
        }
    }

    private String getFileName(Part part) {
        String contentDisp = part.getHeader("content-disposition");
        String[] tokens = contentDisp.split(";");
        for (String token : tokens) {
            if (token.trim().startsWith("filename")) {
                return token.substring(token.indexOf("=") + 2, token.length()-1);
            }
        }
        return "";
    }
}
