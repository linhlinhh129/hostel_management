package com.quanlyphongtro.service.impl;

import com.quanlyphongtro.dao.ContractDAO;
import com.quanlyphongtro.dao.RoomDAO;
import com.quanlyphongtro.model.Contract;
import com.quanlyphongtro.model.Facility;
import com.quanlyphongtro.model.Room;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import java.lang.reflect.Field;
import java.time.LocalDate;
import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
public class ContractServiceImplTest {

    @InjectMocks
    private ContractServiceImpl contractService;

    @Mock
    private ContractDAO contractDAO;

    @Mock
    private RoomDAO roomDAO;

    private Contract validContract;
    private Room validRoom;
    private Facility validFacility;

    @BeforeEach
    void setUp() throws Exception {
        // Inject mocks vào final fields thông qua reflection
        injectMock(contractService, "contractDAO", contractDAO);
        injectMock(contractService, "roomDAO", roomDAO);

        validContract = new Contract();
        validContract.setRoomId(101);
        validContract.setTenantFullName("Nguyen Van A");
        validContract.setTenantIdentityNumber("012345678912");
        validContract.setSignedDate(LocalDate.parse("2024-03-01"));
        validContract.setStartDate(LocalDate.parse("2024-03-01"));
        validContract.setEndDate(LocalDate.parse("2025-03-01"));

        validRoom = new Room();
        validRoom.setId(101);
        validRoom.setCode("P101");

        validFacility = new Facility();
        validFacility.setManagerId(1); // managerId = 1
    }

    private void injectMock(Object target, String fieldName, Object mock) throws Exception {
        Field field = target.getClass().getDeclaredField(fieldName);
        field.setAccessible(true);
        field.set(target, mock);
    }

    // =========================================================
    //  Phase 2 – US1: Tạo hợp đồng (Manager)
    // =========================================================

    @Test
    @DisplayName("TC_CC_01: Tạo hợp đồng thành công với đầy đủ thông tin hợp lệ")
    void testCreateContract_Success() throws Exception {
        when(roomDAO.findById(101)).thenReturn(Optional.of(validRoom));
        when(roomDAO.findFacilityByRoomId(101)).thenReturn(Optional.of(validFacility));
        when(contractDAO.findActiveContractByRoomId(101)).thenReturn(Optional.empty());
        when(contractDAO.create(any(Contract.class))).thenReturn(1);

        contractService.createContract(validContract, 1);

        assertThat(validContract.getStatus()).isEqualTo("ACTIVE");
        assertThat(validContract.getCode()).startsWith("HD-P101-");
        assertThat(validContract.getCreatedBy()).isEqualTo(1);
        assertThat(validContract.getContractId()).isEqualTo(1);

        verify(contractDAO, times(1)).create(validContract);
        verify(roomDAO, times(1)).update(any());
    }

    @Test
    @DisplayName("TC_CC_02: Tạo hợp đồng nhưng bỏ trống Họ tên khách thuê")
    void testCreateContract_MissingTenantName() {
        validContract.setTenantFullName("");

        when(roomDAO.findById(101)).thenReturn(Optional.of(validRoom));
        when(roomDAO.findFacilityByRoomId(101)).thenReturn(Optional.of(validFacility));
        when(contractDAO.findActiveContractByRoomId(101)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> contractService.createContract(validContract, 1))
                .isInstanceOf(Exception.class)
                .hasMessageContaining("Tên người thuê không được để trống");
    }

    @Test
    @DisplayName("TC_CC_03: Tạo hợp đồng nhưng bỏ trống số CMND/CCCD")
    void testCreateContract_MissingIdentity() {
        validContract.setTenantIdentityNumber(null);

        when(roomDAO.findById(101)).thenReturn(Optional.of(validRoom));
        when(roomDAO.findFacilityByRoomId(101)).thenReturn(Optional.of(validFacility));
        when(contractDAO.findActiveContractByRoomId(101)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> contractService.createContract(validContract, 1))
                .isInstanceOf(Exception.class)
                .hasMessageContaining("CCCD không được để trống");
    }

    @Test
    @DisplayName("TC_CC_04: Tạo hợp đồng nhưng chưa chọn phòng thuê (phòng = 0)")
    void testCreateContract_RoomIdZero() {
        validContract.setRoomId(0);
        when(roomDAO.findById(0)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> contractService.createContract(validContract, 1))
                .isInstanceOf(Exception.class)
                .hasMessageContaining("Phòng không tồn tại");
    }

    @Test
    @DisplayName("TC_CC_05: Tạo hợp đồng nhưng bỏ trống Ngày lập hợp đồng")
    void testCreateContract_MissingSignedDate() {
        validContract.setSignedDate(null);

        when(roomDAO.findById(101)).thenReturn(Optional.of(validRoom));
        when(roomDAO.findFacilityByRoomId(101)).thenReturn(Optional.of(validFacility));
        when(contractDAO.findActiveContractByRoomId(101)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> contractService.createContract(validContract, 1))
                .isInstanceOf(Exception.class)
                .hasMessageContaining("Ngày tháng ký/bắt đầu/kết thúc không được để trống");
    }

    @Test
    @DisplayName("TC_CC_08: Chọn phòng không tồn tại trong hệ thống")
    void testCreateContract_RoomNotFound() {
        when(roomDAO.findById(101)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> contractService.createContract(validContract, 1))
                .isInstanceOf(Exception.class)
                .hasMessageContaining("Phòng không tồn tại");
    }

    @Test
    @DisplayName("TC_CC_09: Chọn phòng thuộc cơ sở mà Ban quản lý không có quyền quản lý")
    void testCreateContract_AccessDenied() {
        validFacility.setManagerId(2); // Gán cho Manager khác
        when(roomDAO.findById(101)).thenReturn(Optional.of(validRoom));
        when(roomDAO.findFacilityByRoomId(101)).thenReturn(Optional.of(validFacility));

        assertThatThrownBy(() -> contractService.createContract(validContract, 1))
                .isInstanceOf(Exception.class)
                .hasMessageContaining("Bạn không có quyền tạo hợp đồng cho phòng này");
    }

    @Test
    @DisplayName("TC_CC_10: Chọn phòng đang có một hợp đồng khác ở trạng thái ACTIVE")
    void testCreateContract_ActiveContractExists() {
        when(roomDAO.findById(101)).thenReturn(Optional.of(validRoom));
        when(roomDAO.findFacilityByRoomId(101)).thenReturn(Optional.of(validFacility));
        when(contractDAO.findActiveContractByRoomId(101)).thenReturn(Optional.of(new Contract()));

        assertThatThrownBy(() -> contractService.createContract(validContract, 1))
                .isInstanceOf(Exception.class)
                .hasMessageContaining("Phòng này đang có hợp đồng ACTIVE");
    }

    // =========================================================
    //  Phase 3 – US2: Xem chi tiết hợp đồng (Phân quyền)
    // =========================================================

    @Test
    @DisplayName("T008 [US2] Tenant xem hợp đồng của chính mình (Success)")
    void testViewContract_TenantOwner_Success() {
        when(contractDAO.findByIdAndTenantId(1, 5)).thenReturn(Optional.of(validContract));
        when(roomDAO.findById(101)).thenReturn(Optional.of(validRoom));

        Contract result = contractService.getContractDetailForTenant(1, 5);

        assertThat(result).isNotNull();
        assertThat(result.getRoom()).isNotNull();
        assertThat(result.getRoom().getCode()).isEqualTo("P101");
    }

    @Test
    @DisplayName("T009 [US2] Tenant xem hợp đồng của người khác (Access Denied / Null)")
    void testViewContract_TenantNotOwner_AccessDenied() {
        when(contractDAO.findByIdAndTenantId(1, 99)).thenReturn(Optional.empty());

        Contract result = contractService.getContractDetailForTenant(1, 99);

        assertThat(result).isNull();
    }

    @Test
    @DisplayName("T010 [US2] Manager xem hợp đồng trong cơ sở mình quản lý (Success)")
    void testViewContract_ManagerSameFacility_Success() {
        // getContractDetail() gọi contractDAO.findByIdAndManagerId() – không phải findById()
        when(contractDAO.findByIdAndManagerId(1, 1)).thenReturn(Optional.of(validContract));
        when(roomDAO.findById(101)).thenReturn(Optional.of(validRoom));

        Contract result = contractService.getContractDetail(1, 1);

        assertThat(result).isNotNull();
        assertThat(result.getRoom()).isNotNull();
    }

    @Test
    @DisplayName("T011 [US2] Manager xem hợp đồng ngoài cơ sở mình quản lý (Access Denied)")
    void testViewContract_ManagerOtherFacility_AccessDenied() {
        // managerId=2 không match → DAO trả về empty
        when(contractDAO.findByIdAndManagerId(1, 2)).thenReturn(Optional.empty());

        Contract result = contractService.getContractDetail(1, 2);

        assertThat(result).isNull();
    }

    // =========================================================
    //  Phase 5 – US4: Xóa hợp đồng (Manager)
    // =========================================================

    @Test
    @DisplayName("T015 [US4] Xóa hợp đồng khi trạng thái INACTIVE (Success)")
    void testDeleteContract_Inactive_Success() {
        // verifyContractForDelete() & softDeleteContract() đều delegate thẳng xuống contractDAO
        when(contractDAO.verifyContractForDelete(1, 1))
                .thenReturn(Map.of("status", "success"));
        when(contractDAO.softDeleteContract(1)).thenReturn(true);

        Map<String, String> verify = contractService.verifyContractForDelete(1, 1);
        assertThat(verify.get("status")).isEqualTo("success");

        boolean result = contractService.softDeleteContract(1);
        assertThat(result).isTrue();
    }

    @Test
    @DisplayName("T016 [US4] Xóa hợp đồng khi trạng thái ACTIVE (Fail)")
    void testDeleteContract_Active_Fail() {
        when(contractDAO.verifyContractForDelete(1, 1))
                .thenReturn(Map.of("status", "error", "message", "Hợp đồng đang ACTIVE, không thể xóa"));

        Map<String, String> verify = contractService.verifyContractForDelete(1, 1);
        assertThat(verify.get("status")).isEqualTo("error");
    }
    // =========================================================
    //  TC-NEW-01 – US1: Kiểm tra dữ liệu đầy đủ để in hợp đồng
    // =========================================================

    @Test
    @DisplayName("TC-NEW-01 [US1] Dữ liệu hợp đồng đủ các field để in bản cứng")
    void testPrintContract_DataComplete() {
        // Thiết lập đầy đủ dữ liệu mà template in yêu cầu
        validContract.setAmountInWords("Hai triệu đồng chẵn");
        validContract.setTenantDob(LocalDate.of(2000, 1, 15));
        validContract.setTenantPermanentAddress("123 Đường ABC, Quận 1, TP.HCM");
        validContract.setTenantPhone("0901234567");

        // getContractDetail trả về hợp đồng đã gắn Room
        when(contractDAO.findByIdAndManagerId(1, 1)).thenReturn(Optional.of(validContract));
        when(roomDAO.findById(101)).thenReturn(Optional.of(validRoom));

        Contract result = contractService.getContractDetail(1, 1);

        // Kiểm tra tất cả field cần thiết cho mẫu in không null/rỗng
        assertThat(result).isNotNull();
        assertThat(result.getTenantFullName()).isNotBlank();
        assertThat(result.getTenantIdentityNumber()).isNotBlank();
        assertThat(result.getTenantPermanentAddress()).isNotBlank();
        assertThat(result.getSignedDate()).isNotNull();
        assertThat(result.getStartDate()).isNotNull();
        assertThat(result.getEndDate()).isNotNull();
        assertThat(result.getAmountInWords()).isNotBlank();
        assertThat(result.getRoom()).isNotNull();
        assertThat(result.getRoom().getCode()).isEqualTo("P101");
        // Kiểm tra helper methods cho template in
        assertThat(result.getSignedDay()).isNotBlank();
        assertThat(result.getSignedMonth()).isNotBlank();
        assertThat(result.getSignedYear()).isNotBlank();
        assertThat(result.getFormattedStartDate()).isNotBlank();
        assertThat(result.getFormattedEndDate()).isNotBlank();
    }

    // =========================================================
    //  TC-NEW-02 – US1: Thêm Tenant – Validate email hợp lệ
    // =========================================================

    @Test
    @DisplayName("TC-NEW-02 [US1] Thêm Tenant nhưng email sai định dạng → Exception")
    void testAddTenantFromContract_InvalidEmail_ThrowsException() {
        assertThatThrownBy(() -> contractService.addTenantFromContract(
                1, 101,
                "Nguyen Van A",   // fullName
                "0901234567",     // phone
                "not-an-email",   // email sai định dạng
                "012345678912",   // identityNumber
                "123 ABC",        // permanentAddress
                "MALE",           // gender
                "2000-01-15",     // dobStr
                "2024-03-01",     // contractStartDateStr
                false,            // confirmReactivate
                1,                // managerId
                "http://localhost" // loginLink
        ))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Email không đúng định dạng");
    }

    @Test
    @DisplayName("TC-NEW-03 [US1] Thêm Tenant nhưng Họ tên để trống → Exception")
    void testAddTenantFromContract_EmptyFullName_ThrowsException() {
        assertThatThrownBy(() -> contractService.addTenantFromContract(
                1, 101,
                "",               // fullName trống
                "0901234567",
                "tenant@gmail.com",
                "012345678912",
                "123 ABC",
                "MALE",
                "2000-01-15",
                "2024-03-01",
                false,
                1,
                "http://localhost"
        ))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Họ tên không được để trống");
    }

    @Test
    @DisplayName("TC-NEW-04 [US1] Thêm Tenant nhưng số điện thoại không hợp lệ → Exception")
    void testAddTenantFromContract_InvalidPhone_ThrowsException() {
        assertThatThrownBy(() -> contractService.addTenantFromContract(
                1, 101,
                "Nguyen Van A",
                "12345",           // SĐT sai (không phải 10 số VN)
                "tenant@gmail.com",
                "012345678912",
                "123 ABC",
                "MALE",
                "2000-01-15",
                "2024-03-01",
                false,
                1,
                "http://localhost"
        ))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Số điện thoại không hợp lệ");
    }

    // =========================================================
    //  TC-NEW-05 – US1: Edge Case ngày kết thúc < ngày bắt đầu
    // =========================================================

    @Test
    @DisplayName("TC-NEW-05 [US1] Ngày kết thúc trước ngày bắt đầu → Exception")
    void testCreateContract_EndDateBeforeStartDate() {
        // Đặt endDate trước startDate
        validContract.setEndDate(LocalDate.parse("2023-01-01")); // < startDate 2024-03-01

        when(roomDAO.findById(101)).thenReturn(Optional.of(validRoom));
        when(roomDAO.findFacilityByRoomId(101)).thenReturn(Optional.of(validFacility));
        when(contractDAO.findActiveContractByRoomId(101)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> contractService.createContract(validContract, 1))
                .isInstanceOf(Exception.class);
        // Ghi chú: Service hiện chưa validate endDate > startDate
        // Test này sẽ FAIL và là tín hiệu để team thêm validation vào ContractServiceImpl
    }

    // =========================================================
    //  TC-NEW-06 – US1: getContractForAddTenant khi đã có Tenant
    // =========================================================

    @Test
    @DisplayName("TC-NEW-06 [US1] getContractForAddTenant khi hợp đồng đã có tenant_id → Exception")
    void testGetContractForAddTenant_AlreadyHasTenant_ThrowsException() throws Exception {
        // Giả lập DAO trả về hợp đồng đã có tenantId = 5
        Map<String, Object> contractData = new java.util.HashMap<>();
        contractData.put("contractId", 1);
        contractData.put("tenantId", 5); // đã có tenant
        contractData.put("roomId", 101);

        when(contractDAO.getContractForAddTenant(1, 1)).thenReturn(contractData);

        assertThatThrownBy(() -> contractService.getContractForAddTenant(1, 1))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("Hợp đồng này đã có tài khoản người thuê liên kết");
    }

    @Test
    @DisplayName("TC-NEW-07 [US1] getContractForAddTenant khi hợp đồng chưa có tenant → Trả về Map data")
    void testGetContractForAddTenant_NoTenant_Success() throws Exception {
        Map<String, Object> contractData = new java.util.HashMap<>();
        contractData.put("contractId", 1);
        contractData.put("tenantId", null); // chưa có tenant
        contractData.put("roomId", 101);

        when(contractDAO.getContractForAddTenant(1, 1)).thenReturn(contractData);

        Map<String, Object> result = contractService.getContractForAddTenant(1, 1);

        assertThat(result).isNotNull();
        assertThat(result.get("contractId")).isEqualTo(1);
    }

    // =========================================================
    //  Phase 4 – US3: Extend Contract (Manager Flow)
    // =========================================================

    @Test
    @DisplayName("T012 [US3] Extend Contract - Success")
    void testExtendContract_Success() throws Exception {
        Contract existingContract = new Contract();
        existingContract.setContractId(1);
        existingContract.setTenantId(10);
        existingContract.setRoomId(101);
        existingContract.setEndDate(LocalDate.now().plusMonths(1));
        
        when(contractDAO.findByIdAndManagerId(1, 1)).thenReturn(Optional.of(existingContract));
        when(contractDAO.findActiveContractByRoomId(101)).thenReturn(Optional.of(existingContract));
        
        Room roomWithTenant = new Room();
        roomWithTenant.setId(101);
        roomWithTenant.setTenantId(10);
        when(roomDAO.findById(101)).thenReturn(Optional.of(roomWithTenant));
        
        LocalDate newEndDate = LocalDate.now().plusMonths(3);
        when(contractDAO.extendContractTransaction(1, newEndDate, 10, 101)).thenReturn(true);
        
        contractService.extendContract(1, newEndDate, 1);
        
        verify(contractDAO, times(1)).extendContractTransaction(1, newEndDate, 10, 101);
    }

    @Test
    @DisplayName("T013 [US3] Extend Contract - Invalid Date")
    void testExtendContract_InvalidDate() {
        Contract existingContract = new Contract();
        existingContract.setContractId(1);
        existingContract.setEndDate(LocalDate.now().plusMonths(2));
        
        when(contractDAO.findByIdAndManagerId(1, 1)).thenReturn(Optional.of(existingContract));
        
        LocalDate newEndDate = LocalDate.now().plusMonths(1); // Before current end date
        
        assertThatThrownBy(() -> contractService.extendContract(1, newEndDate, 1))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Ngày hết hạn mới phải sau ngày hết hạn hiện tại");
    }

    @Test
    @DisplayName("T014 [US3] Extend Contract - Not Manager or Not Found")
    void testExtendContract_NotManager() {
        when(contractDAO.findByIdAndManagerId(1, 2)).thenReturn(Optional.empty()); // manager 2 has no access
        
        LocalDate validDate = LocalDate.now().plusMonths(1);
        assertThatThrownBy(() -> contractService.extendContract(1, validDate, 2))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Hợp đồng không tồn tại hoặc bạn không có quyền gia hạn");
    }
}
