package platform.independent.elements.primitives;



public interface Label extends Element {
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
