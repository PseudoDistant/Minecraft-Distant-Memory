package com.mojang.minecraft.gui;

import com.mojang.minecraft.GameSettings;

public final class OptionsScreen extends GuiScreen {

   private static final int MAX_NUM_SETTINGS = 100;
   private static final int BTN_CONTROLS = 100;
   private static final int BTN_DONE = 200;

   private GuiScreen parent;
   private String title = "Options";
   private GameSettings settings;


   public OptionsScreen(GuiScreen var1, GameSettings var2) {
      this.parent = var1;
      this.settings = var2;
   }

   public final void onOpen() {
      for(int i = 0; i < this.settings.settingCount; ++i) {
         this.buttons.add(new OptionButton(i, this.width / 2 - 155 + i % 2 * 160, this.height / 6 + 24 * (i >> 1), this.settings.getSetting(i)));
      }

      this.buttons.add(new Button(BTN_CONTROLS, this.width / 2 - 100, this.height / 6 + 120 + 12, "Controls..."));
      this.buttons.add(new Button(BTN_DONE, this.width / 2 - 100, this.height / 6 + 168, "Done"));
   }

   protected final void onButtonClick(Button button) {
      if(button.active) {
         if(button.id < MAX_NUM_SETTINGS) {
            this.settings.toggleSetting(button.id, 1);
            button.text = this.settings.getSetting(button.id);
         }

         if(button.id == BTN_CONTROLS) {
            this.minecraft.setCurrentScreen(new ControlsScreen(this, this.settings));
         }

         if(button.id == BTN_DONE) {
            this.minecraft.setCurrentScreen(this.parent);
         }

      }
   }

   public final void render(int var1, int var2) {
      drawFadingBox(0, 0, this.width, this.height, 1610941696, -1607454624);
      drawCenteredString(this.fontRenderer, this.title, this.width / 2, 20, 16777215);
      super.render(var1, var2);
   }
}
