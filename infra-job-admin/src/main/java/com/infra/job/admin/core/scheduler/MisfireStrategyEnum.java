package com.infra.job.admin.core.scheduler;

import com.infra.job.admin.core.util.I18nUtil;

public enum MisfireStrategyEnum {

    DO_NOTHING(I18nUtil.getString("misfire_strategy_do_nothing")),

    FIRE_ONCE_NOW(I18nUtil.getString("misfire_strategy_fire_once_now"));

    private String title;

    MisfireStrategyEnum(String title) {
        this.title = title;
    }

    public String getTitle() {
        return title;
    }

    public static MisfireStrategyEnum match(String name, MisfireStrategyEnum defaultItem){
        for (MisfireStrategyEnum item: MisfireStrategyEnum.values()) {
            if (item.name().equals(name)) {
                return item;
            }
        }
        return defaultItem;
    }

}
