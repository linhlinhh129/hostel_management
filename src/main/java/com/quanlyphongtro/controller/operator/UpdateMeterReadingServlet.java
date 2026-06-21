package com.quanlyphongtro.controller.operator;

import com.quanlyphongtro.dto.MeterStatusDTO;
import com.quanlyphongtro.dto.UserSessionDTO;
import com.quanlyphongtro.service.MeterReadingService;

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
                session.setAttribute("error", "Vui lòng nhập Mã phòng.");
                response.sendRedirect(request.getContextPath() + "/operator/meter-readings/update");
                return;
            }

            // Lookup room and previous readings
            MeterStatusDTO previousReading = meterReadingService.getPreviousReadingByRoomCode(roomCode);
            if (previousReading == null) {
                // AC06: Mã phòng không tồn tại
                session.setAttribute("error", "Mã phòng không tồn tại hoặc phòng không ở trạng thái đang thuê.");
                response.sendRedirect(request.getContextPath() + "/operator/meter-readings/update?roomCode=" + roomCode);
                return;
            }

            int roomId = previousReading.getRoomId();
            int prevElectric = previousReading.getPreviousElectricReading() != null ? previousReading.getPreviousElectricReading() : 0;
            int prevWater = previousReading.getPreviousWaterReading() != null ? previousReading.getPreviousWaterReading() : 0;

            // Get new readings
            int newElectric = Integer.parseInt(request.getParameter("newElectric"));
            int newWater = Integer.parseInt(request.getParameter("newWater"));

            // Validation AC02, AC03
            if (newElectric < prevElectric) {
                session.setAttribute("error", "Chỉ số điện không hợp lệ. Số mới (" + newElectric + ") không được nhỏ hơn số cũ (" + prevElectric + ").");
                response.sendRedirect(request.getContextPath() + "/operator/meter-readings/update?roomCode=" + roomCode);
                return;
            }
            if (newWater < prevWater) {
                session.setAttribute("error", "Chỉ số nước không hợp lệ. Số mới (" + newWater + ") không được nhỏ hơn số cũ (" + prevWater + ").");
                response.sendRedirect(request.getContextPath() + "/operator/meter-readings/update?roomCode=" + roomCode);
                return;
            }

            // Handle file uploads (AC04, AC05)
            Part electricPart = request.getPart("electricMeterImage");
            Part waterPart = request.getPart("waterMeterImage");

            if (electricPart == null || electricPart.getSize() == 0) {
                session.setAttribute("error", "Vui lòng tải lên ảnh minh chứng công tơ điện.");
                response.sendRedirect(request.getContextPath() + "/operator/meter-readings/update?roomCode=" + roomCode);
                return;
            }
            if (waterPart == null || waterPart.getSize() == 0) {
                session.setAttribute("error", "Vui lòng tải lên ảnh minh chứng công tơ nước.");
                response.sendRedirect(request.getContextPath() + "/operator/meter-readings/update?roomCode=" + roomCode);
                return;
            }

            String uploadPath = getServletContext().getRealPath("") + File.separator + "uploads" + File.separator + "meter_readings";
            File uploadDir = new File(uploadPath);
            if (!uploadDir.exists()) uploadDir.mkdirs();

            String electricFileName = UUID.randomUUID().toString() + "_" + getFileName(electricPart);
            electricPart.write(uploadPath + File.separator + electricFileName);
            String electricImgUrl = request.getContextPath() + "/uploads/meter_readings/" + electricFileName;

            String waterFileName = UUID.randomUUID().toString() + "_" + getFileName(waterPart);
            waterPart.write(uploadPath + File.separator + waterFileName);
            String waterImgUrl = request.getContextPath() + "/uploads/meter_readings/" + waterFileName;

            // Insert into DB
            boolean success = meterReadingService.insertMeterReading(roomId, newElectric, newWater, electricImgUrl, waterImgUrl, operatorId);

            if (success) {
                // Redirect back to update page for continuous data entry
                response.sendRedirect(request.getContextPath() + "/operator/meter-readings/update");
            } else {
                session.setAttribute("error", "Đã xảy ra lỗi khi lưu dữ liệu. Vui lòng thử lại.");
                response.sendRedirect(request.getContextPath() + "/operator/meter-readings/update?roomCode=" + roomCode);
            }

        } catch (NumberFormatException e) {
            session.setAttribute("error", "Dữ liệu nhập vào không hợp lệ. Vui lòng kiểm tra lại.");
            response.sendRedirect(request.getContextPath() + "/operator/meter-readings/update");
        } catch (Exception e) {
            e.printStackTrace();
            session.setAttribute("error", "Đã xảy ra lỗi hệ thống: " + e.getMessage());
            response.sendRedirect(request.getContextPath() + "/operator/meter-readings/update");
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
