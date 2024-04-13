package util;

import domain.trainer.Trainer;
import repository.TrainerRepository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import static connection.ConnectionUtils.close;
import static connection.ConnectionUtils.getConnection;

public class TrainerUtil {
    public static TrainerRepository trainerRepository = new TrainerRepository();


    // 트레이너와 기간을 입력하면, 오늘부터 입력한 기간 동안의 트레이너의 예약 가능 날자를 List<Boolean>[] 배열에 담아서 반환한다
    public static List<Boolean>[] getTrainerSchedule(Trainer trainer, int period){
        String sql = "select r_time, datediff(r_date, current_date()) as DDay from reservation where r_date > current_date() " +
                "and t_no = ? and datediff(r_date, current_date()) <= ? order by dday";

        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        List<Boolean>[] reservationAvailableDays = new List[period + 1];
        int trainerNum = trainer.getNum();

        for(int i = 0; i < reservationAvailableDays.length; i++){
            reservationAvailableDays[i] = new ArrayList<>();
            for(int j = 0; j < 6; j++){
                reservationAvailableDays[i].add(true);
            }
        }

        try{
            conn = getConnection();
            pstmt = conn.prepareStatement(sql);

            pstmt.setInt(1, trainerNum);
            pstmt.setInt(2, period);

            int subber = trainerRepository.getWorkingHourAdder(trainer);

            rs = pstmt.executeQuery();

            while(rs.next()){
                int DDay = rs.getInt("DDay");
                int time = rs.getInt("r_time") - subber;

                reservationAvailableDays[DDay].set(time, false);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            close(conn, pstmt, rs);
        }

        return reservationAvailableDays;
    }
}
