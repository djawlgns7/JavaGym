package controller.member;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;

import java.io.IOException;

import static util.PageUtil.movePage;

public class HelloMemberController {
    @FXML
    public void myInfo(ActionEvent event) throws IOException {
        movePage(event, "/view/member/myInformation");
    }
}
