import java.time.LocalTime;

public class Shift {
    String day;
    LocalTime startTime;
    LocalTime endTime;

    Long employeeId;

    public Shift(String day){
        // TODO: generate shift id
        this.day = day;
    }
}
