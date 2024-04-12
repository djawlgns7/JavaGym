package controller;

/**
 * 탭 기능 사용 시 탭의 인덱스를 기억하기 위해 사용 (성진)
 */
public class TabController {

    private static TabController instance = new TabController();

    private int selectedTabIndex = 0;

    private TabController() {}

    public static TabController getInstance() {
        return instance;
    }

    public int getSelectedTabIndex() {
        return selectedTabIndex;
    }

    public void setSelectedTabIndex(int index) {
        selectedTabIndex = index;
    }
}