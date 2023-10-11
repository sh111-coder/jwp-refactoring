package kitchenpos.common;

import kitchenpos.domain.MenuGroup;

public class MenuGroupFixtures {

    /**
     * NAME
     */
    public static final String MENU_GROUP1_NAME = "두마리메뉴";

    /**
     * REQUEST
     */
    public static MenuGroup MENU_GROUP1_REQUEST() {
        MenuGroup menuGroup = new MenuGroup();
        menuGroup.setName(MENU_GROUP1_NAME);
        return menuGroup;
    }
}