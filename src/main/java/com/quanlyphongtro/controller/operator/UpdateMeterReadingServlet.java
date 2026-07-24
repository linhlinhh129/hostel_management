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
        String roomCode = request.getParameter("roomCode");
        if (roomCode != null && !roomCode.trim().isEmpty()) {
            request.setAttribute("roomCode", roomCode);
        }
        request.setAttribute("activeMenu", "meter-readings-update");
        request.getRequestDispatcher("/WEB-INF/views/operator/meter_readings/update.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        int operatorId = 1; 
        if (session != null && session.getAttribute("currentUser") != null) {
            UserSessionDTO currentUser = (UserSessionDTO) session.getAttribute("currentUser");
            operatorId = currentUser.getId();
        }

        try {
            String roomCode = request.getParameter("roomCode");
            if (roomCode == null || roomCode.trim().isEmpty()) {
                session.setAttribute("flashMessage", "Vui lòng nhập Mã phòng.");
                session.setAttribute("flashType", "error");
                response.sendRedirect(request.getContextPath() + "/operator/meter-readings");
                return;
            }

            LocalDate now = LocalDate.now();
            int currentMonth = now.getMonthValue();
            int currentYear = now.getYear();

            // Lookup room and previous month's readings
            MeterStatusDTO previousReading = meterReadingService.getReadingBeforeCurrentMonth(roomCode, currentMonth, currentYear);
            if (previousReading == null) {
                // AC06: Mã phòng không tồn tại hoặc chưa từng có dữ liệu trước tháng này
                // Actually if it's the first time ever, previousReading could be null for valid room!
                // Wait, if it's a valid room but first reading ever, we shouldn't block it.
                // Let's check if the room exists using getPreviousReadingByRoomCode just to get roomId if getReadingBeforeCurrentMonth is null.
                previousReading = meterReadingService.getPreviousReadingByRoomCode(roomCode);
                if (previousReading == null) {
                    session.setAttribute("flashMessage", "Mã phòng không tồn tại hoặc phòng không ở trạng thái đang thuê.");
                    session.setAttribute("flashType", "error");
                    response.sendRedirect(request.getContextPath() + "/operator/meter-readings");
                    return;
                } else {
                    // It means this is the VERY FIRST reading in the system, or the reading we got IS the current month's reading.
                    // If it is the current month's reading, then the "previous" before this month is 0.
                    // So we set prevElectric and prevWater to 0, but keep the roomId.
                    previousReading.setPreviousElectricReading(0);
                    previousReading.setPreviousWaterReading(0);
                }
            }

            int roomId = previousReading.getRoomId();
            int prevElectric = previousReading.getPreviousElectricReading() != null ? previousReading.getPreviousElectricReading() : 0;
            int prevWater = previousReading.getPreviousWaterReading() != null ? previousReading.getPreviousWaterReading() : 0;

            // Get new readings
            int newElectric = Integer.parseInt(request.getParameter("newElectric"));
            int newWater = Integer.parseInt(request.getParameter("newWater"));

            // Validation AC02, AC03
            if (newElectric < prevElectric) {
                session.setAttribute("flashMessage", "Chỉ số điện không hợp lệ. Số mới (" + newElectric + ") không được nhỏ hơn số cũ (" + prevElectric + ").");
                session.setAttribute("flashType", "error");
                response.sendRedirect(request.getContextPath() + "/operator/meter-readings");
                return;
            }
            if (newWater < prevWater) {
                session.setAttribute("flashMessage", "Chỉ số nước không hợp lệ. Số mới (" + newWater + ") không được nhỏ hơn số cũ (" + prevWater + ").");
                session.setAttribute("flashType", "error");
                response.sendRedirect(request.getContextPath() + "/operator/meter-readings");
                return;
            }

            // Handle file uploads (AC04, AC05)
            Part electricPart = request.getPart("electricMeterImage");
            Part waterPart = request.getPart("waterMeterImage");

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

            String uploadPath = getServletContext().getRealPath("") + File.separator + "uploads" + File.separator + "meter_readings";
            File uploadDir = new File(uploadPath);
            if (!uploadDir.exists()) uploadDir.mkdirs();

            String electricFileName = UUID.randomUUID().toString() + "_" + getFileName(electricPart);
            electricPart.write(uploadPath + File.separator + electricFileName);
            String electricImgUrl = "/uploads/meter_readings/" + electricFileName;

            String waterFileName = UUID.randomUUID().toString() + "_" + getFileName(waterPart);
            waterPart.write(uploadPath + File.separator + waterFileName);
            String waterImgUrl = "/uploads/meter_readings/" + waterFileName;

            // Check if current month reading exists
            Integer existingMeterId = meterReadingService.checkCurrentMonthReadingExists(roomId, now.getMonthValue(), now.getYear());

            boolean success;
            if (existingMeterId != null) {
                success = meterReadingService.updateMeterReading(existingMeterId, newElectric, newWater, electricImgUrl, waterImgUrl);
            } else {
                success = meterReadingService.insertMeterReading(roomId, newElectric, newWater, electricImgUrl, waterImgUrl, operatorId);
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
