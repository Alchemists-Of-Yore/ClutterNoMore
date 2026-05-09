package dev.tazer.clutternomore.common.compat;

//? fabric {

import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;
import dev.tazer.clutternomore.client.ShapeSwitcherOptionsScreen;

public class ModMenuCompat implements ModMenuApi {
    public ConfigScreenFactory<?> getModConfigScreenFactory() {
        return ShapeSwitcherOptionsScreen::new;
    }
}

//?}