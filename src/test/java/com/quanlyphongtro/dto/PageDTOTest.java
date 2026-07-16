package com.quanlyphongtro.dto;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("PageDTO<T>")
class PageDTOTest {

    private <T> PageDTO<T> page(int page, int pageSize, long total) {
        return new PageDTO<>(List.of(), page, pageSize, total);
    }

    // =========================================================
    // getTotalPages()
    // =========================================================
    @Nested
    @DisplayName("getTotalPages()")
    class TotalPages {
        @ParameterizedTest(name = "total={0}, pageSize={1} → totalPages={2}")
        @CsvSource({
            "0,  10, 1",   // empty → 1
            "10, 10, 1",   // exact fit
            "11, 10, 2",   // overflow → ceil
            "100,10,10",
            "1,  20, 1",
            "21, 10, 3"
        })
        void computesCorrectly(long total, int size, int expected) {
            assertThat(page(1, size, total).getTotalPages()).isEqualTo(expected);
        }

        @Test
        @DisplayName("pageSize=0 → 1 (guard division by zero)")
        void zeroPageSize_returns1() {
            assertThat(page(1, 0, 50).getTotalPages()).isEqualTo(1);
        }
    }

    // =========================================================
    // hasPreviousPage() / hasNextPage()
    // =========================================================
    @Nested
    @DisplayName("hasPreviousPage() / hasNextPage()")
    class Navigation {
        @Test void page1_noPrev()     { assertThat(page(1,  10, 30).hasPreviousPage()).isFalse(); }
        @Test void page2_hasPrev()    { assertThat(page(2,  10, 30).hasPreviousPage()).isTrue();  }
        @Test void lastPage_noNext()  { assertThat(page(3,  10, 30).hasNextPage()).isFalse();     }
        @Test void notLast_hasNext()  { assertThat(page(2,  10, 30).hasNextPage()).isTrue();      }
        @Test void page1_single_noNav() {
            PageDTO<?> p = page(1, 10, 5);
            assertThat(p.hasPreviousPage()).isFalse();
            assertThat(p.hasNextPage()).isFalse();
        }
    }

    // =========================================================
    // getSize() alias
    // =========================================================
    @Test
    @DisplayName("getSize() returns same as getPageSize()")
    void sizeAlias() {
        PageDTO<?> p = page(1, 20, 100);
        assertThat(p.getSize()).isEqualTo(p.getPageSize());
    }
}
