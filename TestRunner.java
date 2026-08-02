import java.time.LocalDate;

public class TestRunner {
    public static void main(String[] args) {
        System.out.println("Running test simulation...");
        try {
            // Simulate calculateUsage
            int prevElec = 9990;
            int currElec = 20;
            String eStatus = "ROLLOVER";
            
            int electricUsage = 0;
            if ("REPLACED".equals(eStatus)) {
                int oldFinal = 10000;
                int newStart = 0;
                electricUsage = (oldFinal - prevElec) + (currElec - newStart);
            } else if ("ROLLOVER".equals(eStatus)) {
                int maxLimit = 10000;
                electricUsage = (maxLimit - prevElec) + currElec;
            } else {
                electricUsage = currElec - prevElec;
            }
            
            System.out.println("Electric Usage: " + electricUsage);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
