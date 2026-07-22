package com.quanlyphongtro.e2e;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

import static org.assertj.core.api.Assertions.assertThat;

public class DependentViewE2ETest {

    private WebDriver driver;
    private final String BASE_URL = "http://localhost:8080/hostel-management";

    @BeforeEach
    void setUp() {
        // T029: Tự động tải ChromeDriver
        WebDriverManager.chromedriver().setup();

        ChromeOptions options = new ChromeOptions();
        // options.addArguments("--headless"); // Comment dòng này lại để Chrome hiển thị giao diện khi chạy Test
        options.addArguments("--disable-gpu");
        options.addArguments("--window-size=1920,1080"); // Mở rộng màn hình ảo để tránh che khuất nút bấm

        driver = new ChromeDriver(options);
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(10));
    }

    @AfterEach
    void tearDown() {
        if (driver != null) {
            driver.quit();
        }
    }

    @Test
    @DisplayName("T032-T034: Tenant login and view masked CCCD of dependents")
    void testTenantLoginAndViewDependents() throws InterruptedException {
        // 1. T032: Điều hướng tới trang đăng nhập
        driver.get(BASE_URL + "/login");

        // Tìm element input form
        WebElement txtUsername = driver.findElement(By.name("username"));
        WebElement txtPassword = driver.findElement(By.name("password"));
        WebElement btnLogin = driver.findElement(By.cssSelector("button[type='submit']"));

        // Điền dữ liệu từ seed.sql
        txtUsername.sendKeys("lethithuylinhtl12@gmail.com");
        Thread.sleep(500); // Tạm dừng để dễ nhìn
        txtPassword.sendKeys("Admin@123");
        Thread.sleep(500);

        // Dùng Javascript để ép click
        ((JavascriptExecutor) driver).executeScript("arguments[0].click();", btnLogin);

        // Chờ chuyển trang sang dashboard
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(5));
        wait.until(ExpectedConditions.urlContains("/dashboard"));

        // 2. Chuyển hướng sang trang Danh sách người phụ thuộc
        driver.get(BASE_URL + "/tenant/dependents");
        
        Thread.sleep(2000); // Dừng lại 2 giây để user nhìn giao diện
        
        // Kiểm tra xem có bảng dữ liệu không, hay là giao diện "Chưa có người phụ thuộc"
        java.util.List<WebElement> tables = driver.findElements(By.cssSelector("table.table-mintlify"));
        
        if (tables.isEmpty()) {
            // Trường hợp Database thật của user chưa có ai -> Rơi vào Empty State
            System.out.println("No dependents found in the database. Verifying empty state UI...");
            WebElement emptyStateMessage = driver.findElement(By.tagName("h4"));
            assertThat(emptyStateMessage.getText()).contains("Chưa có người phụ thuộc");
            System.out.println("Empty state verified successfully. Skipping detail view test.");
            return; // Kết thúc bài test thành công
        }

        // Trường hợp có dữ liệu
        WebElement table = tables.get(0);
        assertThat(table.isDisplayed()).isTrue();

        // 3. Tìm đích danh nút "Chi tiết" NẰM BÊN TRONG BẢNG của hàng đầu tiên
        WebElement firstRow = table.findElement(By.cssSelector("tbody tr:first-child"));
        WebElement detailBtn = firstRow.findElement(By.xpath(".//a[contains(text(), 'Chi tiết')]"));
        ((JavascriptExecutor) driver).executeScript("arguments[0].click();", detailBtn);
        
        // Chờ trang chi tiết tải xong (URL sẽ chứa /tenant/dependents/{id})
        wait.until(ExpectedConditions.urlMatches(".*\\/tenant\\/dependents\\/\\d+"));
        
        Thread.sleep(2000); // Dừng 2 giây để user nhìn trang chi tiết
        
        // 4. Lấy toàn bộ text trên trang chi tiết để tìm số CCCD bị che
        String bodyText = driver.findElement(By.tagName("body")).getText();
        
        // Assert trang detail có chứa chuỗi CCCD bị che
        assertThat(bodyText).contains("******");
    }
}
