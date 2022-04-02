package com.e.organizer_app;

import java.util.concurrent.atomic.AtomicInteger;

public class NotificationID {
    private final static AtomicInteger c = new AtomicInteger(0);
    // Метод класса позволяет сформировать уникальное id для уведомлений
    public static int getID() {
        return c.incrementAndGet();
    }
}
