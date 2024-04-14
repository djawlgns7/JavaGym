package controller.payment;


public class PaymentTab {

    private static PaymentTab instance = new PaymentTab();

    private int selectedTabIndex = 0;

    private PaymentTab() {}

    public static PaymentTab getInstance() {
        return instance;
    }

    public int getSelectedTabIndex() {
        return selectedTabIndex;
    }

    public void setSelectedTabIndex(int index) {
        selectedTabIndex = index;
    }
}
