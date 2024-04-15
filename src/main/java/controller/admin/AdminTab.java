package controller.admin;

/**
 * 탭 기능 사용 시 탭의 인덱스를 기억하기 위해 사용 (성진)
 */
public class AdminTab {

    private static AdminTab instance = new AdminTab();

    private int selectedTabIndex = 0;

    private AdminTab() {}

    public static AdminTab getInstance() {
        return instance;
    }

    public int getSelectedTabIndex() {
        return selectedTabIndex;
    }

    public void setSelectedTabIndex(int index) {
        selectedTabIndex = index;
    }
}