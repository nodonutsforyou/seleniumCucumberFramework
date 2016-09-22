package web.core.utilities;

import com.sun.jna.Native;
import com.sun.jna.Pointer;
import com.sun.jna.win32.StdCallLibrary;
import com.sun.jna.win32.W32APIOptions;

import static com.sun.jna.platform.win32.WinDef.HWND;
import static com.sun.jna.platform.win32.WinDef.LPARAM;

/**
 * Класс для работы с окнами операционной системы. Использует WinAPI
 * библиотеки User32.
 */
class IterateAndCloseChildWindows {

    /**
     * Определяем интерфейс, который будет использовать после загрузки библиотеки
     * User32, оставляя только самые необходимые методы (остальное нам не нужно)
     */
    public interface User32 extends StdCallLibrary {
        User32 INSTANCE = (User32) Native.loadLibrary("user32", User32.class, W32APIOptions.DEFAULT_OPTIONS);

        boolean PostMessage(HWND hWnd, int msg, int wParam, int lParam);

        boolean EnumWindows(WNDENUMPROC lpEnumFunc, Pointer arg);

        boolean EnumChildWindows(HWND parent, WNDENUMPROC callback, LPARAM info);

        interface WNDENUMPROC extends StdCallCallback {
            boolean callback(HWND hWnd, Pointer arg);
        }

        int GetWindowTextA(HWND hWnd, byte[] lpString, int nMaxCount);

    }

    /**
     * Тестовый метод
     */
    public static void main(String[] args) {
        IterateAndCloseChildWindows windows = new IterateAndCloseChildWindows();
        windows.findWindowsByNameAndClickOnChildElements("Security Warning", "Yes");
    }

    /**
     * Производит действие с окном - нажатие на одного из его потомков.
     * Методу передается название окна и он найдет все окна
     * с таким названием, после чего у каждого найденного окна будет произведен поиск
     * всех его окон-потомков (кнопка - тоже окно). На все найденные таким образом
     * элементы будет произведено нажатие.
     *
     * @param windowName Название окна.
     * @param buttonName Название элемента-потомка(окна) на которого будет произведено нажатие.
     * @return True - Было хотя бы одно нажатие. False - не одного нажатия
     */
    public boolean findWindowsByNameAndClickOnChildElements(final String windowName, final String buttonName) {
        final boolean[] flag = {false};
        if (windowName == null || buttonName == null) {
            return false;
        }
        // Метод ищет все окна методом EnumWindows() и обрабатывает найденные методом callback().
        // Параметр hWnd метода callback() это HWND окна, которое было надено.
        User32.INSTANCE.EnumWindows(new User32.WNDENUMPROC() {
            public boolean callback(HWND hWnd, Pointer userData) {
                byte[] textBuffer = new byte[512];
                User32.INSTANCE.GetWindowTextA(hWnd, textBuffer, 512);
                String wText = Native.toString(textBuffer);

                if (!wText.equals(windowName)) {
                    return true;
                }

                // поиск всех родительских окон окна с hWnd
                User32.INSTANCE.EnumChildWindows(hWnd, new User32.WNDENUMPROC() {
                    public boolean callback(HWND hWnd, Pointer userData) {
                        byte[] textBuffer = new byte[512];
                        User32.INSTANCE.GetWindowTextA(hWnd, textBuffer, 512);
                        if (new String(textBuffer).trim().equals(buttonName)) {
                            User32.INSTANCE.PostMessage(hWnd, 0x0201, 0x0001, 0);
                            User32.INSTANCE.PostMessage(hWnd, 0x0100, 0x0D, 0);
                            flag[0] = true;
                            return true;
                        }
                        return true;
                    }
                }, null);
                return true;
            }
        }, null);

        return flag[0];
    }
}