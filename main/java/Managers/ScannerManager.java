package Managers;

import java.util.Objects;
import java.util.Scanner;

public final class ScannerManager {
    private static ScannerManager instance;
    private Scanner userScanner;

    private ScannerManager(Scanner initialScanner) {
        this.userScanner = Objects.requireNonNull(initialScanner);
    }

    public static synchronized ScannerManager init(Scanner initialScanner) {
        if (instance == null) {
            instance = new ScannerManager(initialScanner);
        }
        return instance;
    }

    public static ScannerManager getScannerManager() {
        if (instance == null) {
            throw new IllegalStateException("ScannerManager не инициализирован");
        }
        return instance;
    }

    public Scanner getUserScanner() {
        return userScanner;
    }

    public void setUserScanner(Scanner userScanner) {
        this.userScanner = Objects.requireNonNull(userScanner);
    }
}