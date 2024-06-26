package util;

import domain.Item;

import java.util.Arrays;
import java.util.List;

import static util.MemberUtil.getRemain;

public class ProcedureUtil {

    public static List<Integer> getRemainAll(int memberNum) {

        Integer remainGym = getRemain(memberNum, Item.GYM_TICKET);
        Integer remainPT = getRemain(memberNum, Item.PT_TICKET);
        Integer remainClothes = getRemain(memberNum, Item.CLOTHES);
        Integer remainLocker = getRemain(memberNum, Item.LOCKER);

        return Arrays.asList(remainGym, remainPT, remainClothes, remainLocker);
    }
}

