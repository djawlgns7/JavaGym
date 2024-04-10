package domain.member;

import lombok.Data;

/**
 * 관리자 페이지에서 사용 (성진)
 */
@Data
public class UsingLocker {

    private int count;
    private int memberNum;
    private String memberName;
    private int lockerNum;
    private int lockerPeriod;
}