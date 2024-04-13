package repository;

import java.util.HashMap;
import java.util.Map;

public class CodeStore {

    private static CodeStore codeStore;
    public boolean phoneCheck = false;
    public boolean codeCheck = false;
    public boolean isSend = false;

    // 싱글톤으로 관리
    private CodeStore() {
    }

    // 멀티 쓰레드 환경에서도 안전하게 작동하도록 동기화 처리
    public static synchronized CodeStore getInstance() {
        if (codeStore == null) {
            codeStore = new CodeStore();
        }
        return codeStore;
    }

    private Map<String, Integer> store = new HashMap<>();

    public void storeCode(String phone, int code) {
        store.put(phone, code);
    }

    public boolean verifyCode(String phone, int inputCode) {
        if (store.containsKey(phone)) {
            int storedCode = store.get(phone);
            return storedCode == inputCode;
        }
        return false;
    }

    public void removeCode(String phone) {
        store.remove(phone);
    }
}
