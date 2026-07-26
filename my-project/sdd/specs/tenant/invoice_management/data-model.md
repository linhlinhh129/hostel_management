# Data Model: Tenant Invoice Utility Meter Photos

## Entity & DTO Extensions

### `InvoiceDetailDTO` (Data Transfer Object)
Model truyền dữ liệu cho màn hình Chi tiết Hóa đơn phía Tenant (`/tenant/invoice-detail`).

```java
public class InvoiceDetailDTO {
    // Existing fields...
    private String electricImg; // Đường dẫn URL ảnh chụp đồng hồ điện (ví dụ: /uploads/meters/elec_123.jpg)
    private String waterImg;    // Đường dẫn URL ảnh chụp đồng hồ nước (ví dụ: /uploads/meters/water_123.jpg)

    public String getElectricImg() { return electricImg; }
    public void setElectricImg(String electricImg) { this.electricImg = electricImg; }

    public String getWaterImg() { return waterImg; }
    public void setWaterImg(String waterImg) { this.waterImg = waterImg; }
}
```

### Table Relationships
```sql
invoices i
  LEFT JOIN meter_readings mr ON i.meter_id = mr.meter_id
  -- Truy vấn lấy mr.electric_img và mr.water_img truyền sang InvoiceDetailDTO
```
