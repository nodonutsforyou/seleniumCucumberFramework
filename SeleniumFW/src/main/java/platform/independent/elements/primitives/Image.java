package platform.independent.elements.primitives;

/**
 * Created by MVostrikov on 23.07.2015.
 */
public interface Image extends Element {
    /**
     * Получает текст лейбла.
     *
     * @return Возвращает текст лейбла.
     */
    String getText();

    /**
     * Получает содержимое лэйбла
     *
     * @return содержимое лэйбла
     */
    String getInnerHTML();

    String getAttribute(String attr);
}
