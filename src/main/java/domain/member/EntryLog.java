package domain.member;

import lombok.Data;

import java.sql.Timestamp;

@Data
public class EntryLog {
    private int entryNum;
    private int memberNum;
    private Timestamp entryTime;
}